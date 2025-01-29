package com.example.point.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "USER_POINT")
@Data
public class UserPoint {
    @Id
    @Column(name = "user_point_key")
    private Long userPointKey;

    @Column(name = "user_key")
    private String userKey;

    @Column(name = "MAX_POINT")
    private Double maxPoint;

    @Column(name = "IS_LOCK")
    private boolean isLock;
}
