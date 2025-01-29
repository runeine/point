package com.example.point.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.point.entity.Point;

import jakarta.persistence.LockModeType;

public interface PointRepository extends JpaRepository<Point, Long> {

    @Query("SELECT SUM(p.remainAmount) FROM Point p WHERE p.userKey = :userKey AND p.expiredDate >= CURRENT_DATE")
    Optional<Double> findTotalRemainAmountByUserKey(@Param("userKey") String userKey);

    List<Point> findAvailablePointsByUserKey(String userKey);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Point p WHERE p.userKey = :userKey AND p.expiredDate >= CURRENT_DATE ORDER BY p.isAdmin DESC, p.expiredDate ASC")
    List<Point> findPointsByUserKeyOrderByAdminAndExpiry(@Param("userKey") String userKey);

    

}
