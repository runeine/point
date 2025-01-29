package com.example.point.dto;

import java.util.Date;

import lombok.Data;

@Data
public class PointRequest {

    private String pointTransactionKey; // 포인트 거래 key
    private String pointKey; // 포인트 key
    private String userKey; // 사용자 key
    private double amount; // 거래 금액
    private String transactionTypeDetail; // 거래 타입
    private Date transactionDate; // 거래 일자
    private String orderKey; // 주문 key
    private String expiredDays; // 유효기간
    
    


}
