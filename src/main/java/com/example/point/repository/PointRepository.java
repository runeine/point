package com.example.point.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.point.entity.Point;

public interface PointRepository extends JpaRepository<Point, Long> {

}
