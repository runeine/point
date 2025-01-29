package com.example.point.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.point.dto.PointRequest;
import com.example.point.entity.Point;
import com.example.point.entity.TransactionTypeDetail;
import com.example.point.repository.PointRepository;
import com.example.point.repository.PointTransactionRepository;
import com.example.point.repository.UserPointRepository;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class PointServiceImplTest {

    @Mock
    private PointRepository pointRepository;

    @Mock
    private PointTransactionRepository pointTransactionRepository;

    @Mock
    private UserPointRepository userPointRepository;

    @InjectMocks
    private PointServiceImpl pointService;

    @Value("${point.min-amount:1}")
    private double minAmount;

    @Value("${point.max-amount:100000}")
    private double maxAmount;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        assertNotNull(pointRepository);
        assertNotNull(pointTransactionRepository);
        assertNotNull(userPointRepository);
        assertNotNull(pointService);
    }

    @Test
    public void testSavePoint_Success() throws Exception {
        PointRequest request = new PointRequest();
        request.setUserKey("user123");
        request.setAmount(5000.0);
        request.setTransactionTypeDetail("NORMAL_SAVE");

        Point savedPoint = pointService.savePoint(request);

        assertNotNull(savedPoint);
        assertEquals("user123", savedPoint.getUserKey());
        assertEquals(5000.0, savedPoint.getRemainAmount());
        assertEquals(TransactionTypeDetail.NORMAL_SAVE, savedPoint.getTransactionTypeDetail());
    }

    @Test
    public void testSavePoint_AmountExceedsMax() {
        PointRequest request = new PointRequest();
        request.setUserKey("user123");
        request.setAmount(maxAmount + 1);
        request.setTransactionTypeDetail("NORMAL_SAVE");

        Exception exception = assertThrows(Exception.class, () -> {
            pointService.savePoint(request);
        });

        assertEquals("포인트 지급 금액은 " + minAmount + "~" + maxAmount + " 사이여야 합니다.", exception.getMessage());
    }

    @Test
    public void testSavePoint_AmountBelowMin() {
        PointRequest request = new PointRequest();
        request.setUserKey("user123");
        request.setAmount(minAmount - 1);
        request.setTransactionTypeDetail("NORMAL_SAVE");

        Exception exception = assertThrows(Exception.class, () -> {
            pointService.savePoint(request);
        });

        assertEquals("포인트 지급 금액은 " + minAmount + "~" + maxAmount + " 사이여야 합니다.", exception.getMessage());
    }

}