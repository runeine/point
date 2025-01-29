package com.example.point.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.point.dto.PointRequest;
import com.example.point.dto.PointResponse;
import com.example.point.entity.Point;
import com.example.point.entity.PointTransaction;
import com.example.point.entity.TransactionType;
import com.example.point.entity.TransactionTypeDetail;
import com.example.point.entity.UserPoint;
import com.example.point.repository.PointRepository;
import com.example.point.repository.PointTransactionRepository;
import com.example.point.repository.UserPointRepository;

import io.micrometer.common.util.StringUtils;

@Service
public class PointServiceImpl implements PointService {

    @Value("${point.min-amount}")
    private double minAmount; // 최소 포인트 지급 금액

    @Value("${point.max-amount}")
    private double maxAmount; // 최대 포인트 지급 금액

    @Value("${point.defaultUserMaxAmount}")
    private double userMaxAmount; // 최대 포인트 지급 금액


    private final PointRepository pointRepository;

    private final PointTransactionRepository pointTransactionRepository;

    private final UserPointRepository userPointRepository;

    public PointServiceImpl(PointRepository pointRepository, PointTransactionRepository pointTransactionRepository, UserPointRepository userPointRepository){
        this.pointRepository = pointRepository;
        this.pointTransactionRepository = pointTransactionRepository;
        this.userPointRepository = userPointRepository;
    }

    @Override
    @Transactional
    public Point savePoint(PointRequest request) throws Exception {
        // 1~100,000 사이의 포인트만 지급 가능
        // 최대 포인트 지급 금액은 하드코딩이 아닌 설정 가능
        // 개인별 최대 금액 설정 가능
        // 적립된 금액에서 사용되는 사용 내역이 확인이 가능해야한다. 적립 시에는 사용 내역 없이 적립이 가능하나 사용 시 확인이 가능하도록 적립 내역이 필요하다.
        // 수기 지급 포인트는 다른 적립과 구분이 가능하다.
        // 만료일은 기본 365일 이며 , 최소 1일에서 최대 5년 미난 날짜까지 설정 가능하다.
        String userKey = request.getUserKey();        
        double amount = request.getAmount();

        UserPoint userPoint = userPointRepository.findByUserKey(userKey);

        if(userPoint != null){
            userMaxAmount = userPoint.getMaxPoint();           
        }       

        if (minAmount == 0) {
            minAmount = 1; // default value for maxAmount
        }
        if (maxAmount == 0) {
            maxAmount = 100000; // default value for maxAmount
        }

        if (userMaxAmount == 0) {
            userMaxAmount = 2000000; // default value for maxAmount
        }

        if(amount < minAmount || amount >= maxAmount){
            throw new Exception("포인트 지급 금액은 " + minAmount + "~" + maxAmount + " 사이여야 합니다.");
        }

        String transactionType = request.getTransactionTypeDetail();

        TransactionTypeDetail transactionTypeEnum = TransactionTypeDetail.valueOf(transactionType);   
        boolean isAdmin = transactionTypeEnum.isAdmin();

        double userRemainAmunt = pointRepository.findTotalRemainAmountByUserKey(userKey).orElse(0d);
        
        double afterCal = amount+ userRemainAmunt;

        if (afterCal > 100000){
            throw new Exception("최대 적립금액을 초과하였습니다.");
        }

        if( afterCal > userMaxAmount){
            throw new Exception("최대 보유 가능 금액을 초과하였습니다.");
        }

        String expiredDays = request.getExpiredDays();

        long day = 24*60*60*1000; // 1일 단위        

        if(StringUtils.isBlank(expiredDays)){
            expiredDays = "365";
        }

        long expiredDate = Long.parseLong(expiredDays);

        // 최소 1일에서 최대 5년 미만 날짜까지 설정 가능하다.
        if (expiredDate < 1 || expiredDate >= 365 * 5) {
            throw new Exception("만료일은 최소 1일에서 최대 5년 미만이어야 합니다.");
        }

        

       Point point = Point.builder()
       .initAmount(amount)
       .remainAmount(amount)
       .userKey(request.getUserKey())
       .expiredDate(new Date(System.currentTimeMillis() + (expiredDate * day)))
       .transactionTypeDetail(transactionTypeEnum)
       .isAdmin(isAdmin)
       .build();
       
       pointRepository.save(point);

       PointTransaction pointTransaction = PointTransaction.builder()
       .pointKey(point)
       .amount(amount)
       .orderKey(request.getOrderKey())
       .transactionDate(new Date())
       .transactionType(TransactionType.SAVE)
       .transactionTypeDetail(transactionTypeEnum)
       .build();

        pointTransactionRepository.save(pointTransaction);

        return point;

            
    }


    @Override
    @Transactional
    public PointResponse usePoint(PointRequest request) throws Exception {
        String userKey = request.getUserKey();        
        double amount = request.getAmount();
        String transactionType = request.getTransactionTypeDetail();

        // 차감 가능한 포인트 조회
        List<Point> availableList = pointRepository.findPointsByUserKeyOrderByAdminAndExpiry(userKey);
        
        // 회원 잔액 조회
        double userRemainAmunt = pointRepository.findTotalRemainAmountByUserKey(userKey).orElse(0d);

        // 잔액보다 요청 금액이 큰 경우 에러 처리
        if (amount > userRemainAmunt) {
            throw new Exception("사용할 수 있는 포인트가 부족합니다.");
        }

        // 사용할 포인트 set
        double remainingAmountToUse = amount;

        // 차감 가능한 포이트 대상으로 차감
        for (Point point : availableList) {
            if (remainingAmountToUse <= 0) {
                break;
            }

            // 포인트 별 현재 잔액 조회
            double pointRemainAmount = point.getRemainAmount();
            double txAmount;

            // 사용할 금액 포다 조회 된 금액이 더 크면 차감하고 사용할 금액 0으로 마감
            if (pointRemainAmount >= remainingAmountToUse) {
                point.setRemainAmount(pointRemainAmount - remainingAmountToUse);
                txAmount = remainingAmountToUse;
                remainingAmountToUse = 0;
                
            } else {
                // 사용할 금액이 남았으면 현재 조회 잔액은 0으로 만들고, 다음 포인트로 넘어가기 위한 사용 금액 SET
                point.setRemainAmount(0);
                txAmount = pointRemainAmount;
                remainingAmountToUse -= pointRemainAmount;
            }

            // 변경된 잔액 update
            pointRepository.save(point);

            // 사용 내역 저장
            TransactionTypeDetail transactionTypeEnum = TransactionTypeDetail.valueOf(transactionType);
            pointTransactionRepository.save(PointTransaction.builder()
                .pointKey(point)
                .amount(txAmount)
                .orderKey(request.getOrderKey())
                .transactionDate(new Date())
                .transactionType(TransactionType.USE)
                .transactionTypeDetail(transactionTypeEnum)
                .build()
            );
        }

        if (remainingAmountToUse > 0) {
            throw new RuntimeException("사용할 수 있는 포인트가 부족합니다.");
        }

        return PointResponse.success("SUCCESS","사용 처리되었습니다.",null);
    }


    @Override
    @Transactional
    public Point cancelSavedPoint(PointRequest request) throws Exception {
        
        // 원거래 조회
        /*
        String originTxKey = request.getPointTransactionKey();
        
        Long txkey = Long.parseLong(originTxKey);
        PointTransaction originTx = pointTransactionRepository.findById(txkey).orElse(null);
        if (originTx == null) {
            throw new RuntimeException("취소할 포인트 내역이 존재하지 않습니다.");
        }
         */
        
        
         
        

        Optional<Point> originPointOptional = pointRepository.findById(Long.parseLong(request.getPointKey()));
        if (!originPointOptional.isPresent()) {
            throw new RuntimeException("취소할 포인트 내역이 존재하지 않습니다.");
        }
        Point originPoint = originPointOptional.get();
        
        
        // 잔액 조회
        Double remainAmount = originPoint.getRemainAmount();
        
        // 적립 금액 중 사용된 금액이 있으면 에러처리
        List<PointTransaction> ptList = pointTransactionRepository.findByPointKeyAndTransactionType(originPoint, TransactionType.USE);

        if(!ptList.isEmpty()){
            throw new RuntimeException("이미 사용된 포인트가 있어 취소할 수 없습니다.");
        }

        // 원 금액으로 복구
        originPoint.setRemainAmount(0);
        pointRepository.save(originPoint);

        PointTransaction pointTransaction  = PointTransaction.builder()
            .pointKey(originPoint)
            .amount(remainAmount)
            .orderKey(request.getOrderKey())
            .transactionDate(new Date())
            .transactionType(TransactionType.USE)
            .transactionTypeDetail(TransactionTypeDetail.CANCEL_SAVE)

            .build();
        // 취소 내역 저장
        pointTransactionRepository.save(pointTransaction);

        return originPoint;
    }
    

    @Override
    @Transactional
    public PointResponse cancelUsedPoint(PointRequest request) throws Exception {
        
        // 사용 취소는 주문 key 기반으로 취소
        // 하나의 주문에 여러 개의 Point 적립 내용이 사용되었을 수 있다.
        String orderKey  = request.getOrderKey();

        List<PointTransaction> originTx = pointTransactionRepository.findByOrderKeyAndTransactionType(orderKey, TransactionType.USE);
        if (originTx.isEmpty()) {
            throw new RuntimeException("취소할 포인트 내역이 존재하지 않습니다.");
        }

       

        List<Point> originPoints = originTx.stream().map(PointTransaction::getPointKey).collect(Collectors.toList());
        
        double cancelRequestAmount = request.getAmount();

        for (Point originPoint : originPoints) {
            double usedAmount = originPoint.getInitAmount() - originPoint.getRemainAmount();
            if (usedAmount <= 0) {
                continue; // 이미 사용된 포인트가 없으면 다음으로 넘어감
            }

            double amountToRestore = Math.min(usedAmount, cancelRequestAmount);
            originPoint.setRemainAmount(originPoint.getRemainAmount() + amountToRestore);
            cancelRequestAmount -= amountToRestore;

            // 유효기간이 지난 경우 신규 Point로 생성
            if (originPoint.getExpiredDate().before(new Date())) {
                Point newPoint = Point.builder()
                        .initAmount(amountToRestore)
                        .remainAmount(amountToRestore)
                        .userKey(originPoint.getUserKey())
                        .expiredDate(new Date(System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000)) // 기본 만료일 365일
                        .transactionTypeDetail(TransactionTypeDetail.CANCEL_SAVE)
                        .isAdmin(false)
                        .build();
                pointRepository.save(newPoint);
                
                // 취소 내역 저장
                pointTransactionRepository.save(PointTransaction.builder()
                .pointKey(newPoint)
                .amount(amountToRestore)
                .orderKey(request.getOrderKey())
                .transactionDate(new Date())
                .transactionType(TransactionType.SAVE)
                .transactionTypeDetail(TransactionTypeDetail.CANCEL_USE)
                .build());

            } else {
                pointRepository.save(originPoint);
                pointTransactionRepository.save(PointTransaction.builder()
                .pointKey(originPoint)
                .amount(amountToRestore)
                .orderKey(request.getOrderKey())
                .transactionDate(new Date())
                .transactionType(TransactionType.SAVE)
                .transactionTypeDetail(TransactionTypeDetail.CANCEL_USE)
                .build());
            }

            if (cancelRequestAmount <= 0) {
                break; // 취소 요청 금액을 모두 처리했으면 종료
            }
        }

        return PointResponse.success("SUCCESS","취소 처리되었습니다.",null);
    }
}
