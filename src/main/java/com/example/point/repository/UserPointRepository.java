package com.example.point.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.example.point.entity.UserPoint;

public interface UserPointRepository extends JpaRepository<UserPoint, String>{

    UserPoint findByUserKey(@Param("userKey") String userKey);

}
