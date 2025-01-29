package com.example.point.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.point.dto.PointRequest;
import com.example.point.dto.PointResponse;
import com.example.point.entity.Point;
import com.example.point.repository.PointRepository;
import com.example.point.service.PointService;

@RestController
@RequestMapping("/api/points")
public class PointController {

    private static final Logger logger = LoggerFactory.getLogger(PointController.class);

    private final PointRepository pointRepository;
    private final PointService pointService;

    public PointController(PointRepository pointRepository, PointService pointService) {
        this.pointRepository = pointRepository;
        this.pointService = pointService;
    }

    @GetMapping
    public ResponseEntity<List<Point>> getAllPoints() {
        List<Point> points = pointRepository.findAll();
        return ResponseEntity.ok(points);
    }

    @PostMapping("/save")
    public ResponseEntity<Point> savePoint(@RequestBody PointRequest request) {
        Point savedPoint = null;
        logger.info("start savePoint: {}", request);

        try {
            savedPoint = pointService.savePoint(request);
            if (savedPoint == null) {
                logger.error("Failed to save point: {}", request);
                return ResponseEntity.badRequest().body(null);
            }
        } catch (Exception e) {
            logger.error("Exception occurred while saving point: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
        
        return ResponseEntity.ok(savedPoint);
    }

    @PostMapping("/use")
    public ResponseEntity<PointResponse> usePoint(@RequestBody PointRequest request) {
        PointResponse result = null;
        logger.info("start usePoint: {}", request);

        try {
            result = pointService.usePoint(request);
           
        } catch (Exception e) {
            logger.error("Exception occurred while using point: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
        
        return ResponseEntity.ok(result);
    }

    @PostMapping("/save/cancel")
    public ResponseEntity<Point> cancelSavedPoint(@RequestBody PointRequest request) {
        Point savedPoint = null;
        logger.info("start savePoint: {}", request);

        try {
            savedPoint = pointService.cancelSavedPoint(request);
            if (savedPoint == null) {
                logger.error("Failed to cancel saved point: {}", request);
                return ResponseEntity.badRequest().body(null);
            }
        } catch (Exception e) {
            logger.error("Exception occurred while canceling saved point: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
        
        return ResponseEntity.ok(savedPoint);
    }

    @PostMapping("/use/cancel")
    public ResponseEntity<PointResponse> cancelUsedPoint(@RequestBody PointRequest request) {
        PointResponse result = null;
        logger.info("start usePoint: {}", request);

        try {
            result = pointService.cancelUsedPoint(request);
            if (result == null) {
                logger.error("Failed to cancel used point: {}", request);
                return ResponseEntity.badRequest().body(null);
            }
        } catch (Exception e) {
            logger.error("Exception occurred while canceling used point: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
        
        return ResponseEntity.ok(result);
    }

}