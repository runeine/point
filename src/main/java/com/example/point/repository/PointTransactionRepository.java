package com.example.point.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.example.point.entity.Point;
import com.example.point.entity.PointTransaction;
import com.example.point.entity.TransactionType;

public interface PointTransactionRepository extends JpaRepository<PointTransaction, Long> {

   List<PointTransaction> findByPointKeyAndTransactionType(@Param("pointKey") Point pointKey, @Param("transactionType") TransactionType transactionType);
   
   List<PointTransaction> findByOrderKeyAndTransactionType(@Param("orderKey") String orderKey, @Param("transactionType") TransactionType transactionType);

}
