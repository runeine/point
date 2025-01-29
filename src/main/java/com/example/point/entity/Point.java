package com.example.point.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "point")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Point {

    @Id
    @Column(name = "point_key")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pointKey; // Change to String

    @Column(name = "user_key")
    private String userKey;
    @Column(name = "remain_amount")
    private double remainAmount;
    @Column(name = "expired_date")
    private Date expiredDate;
    @Column(name = "init_amount")
    private double initAmount;
    @Column(name = "tx_type_detail")
    private TransactionTypeDetail transactionTypeDetail;
    @Column(name = "is_admin")
    private boolean isAdmin; // 관리자 여부
    @Version
    private Long version;
}
