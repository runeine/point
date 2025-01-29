package com.example.point.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "point_transaction")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PointTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_transaction_key")
    private Long pointTransactionKey; // 포인트 거래 key


    @ManyToOne(targetEntity = Point.class)
    @JoinColumn(name = "point_key")
    private Point pointKey; // 포인트 key

    @Column(name = "amount")
    private double amount; // 거래 금액

    @Column(name = "tx_type") // 거래 구분
    private TransactionType transactionType;

    @Column(name = "tx_type_detail")
    private TransactionTypeDetail transactionTypeDetail; // 거래 구분 상세
    
    @Column(name = "transaction_date")
    private Date transactionDate; // 거래 일자

    @Column(name = "order_key")
    private String orderKey; // 주문 key

    

}
