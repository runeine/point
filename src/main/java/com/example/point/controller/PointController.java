package com.example.point.controller;

import com.example.point.entity.Point;
import com.example.point.repository.PointRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/points")
public class PointController {

    private final PointRepository pointRepository;

    public PointController(PointRepository pointRepository) {
        this.pointRepository = pointRepository;
    }

    @GetMapping
    public ResponseEntity<List<Point>> getAllPoints() {
        List<Point> points = pointRepository.findAll();
        return ResponseEntity.ok(points);
    }
}