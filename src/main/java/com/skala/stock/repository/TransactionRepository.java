package com.skala.stock.repository;

import com.skala.stock.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserIdOrderByTransactionDateDesc(Long userId);

    List<Transaction> findByUserIdAndStockIdOrderByTransactionDateDesc(Long userId, Long stockId);

    // 거래 상세 단건 조회 (user, stock 같이 로딩)
    @Query("SELECT t FROM Transaction t " +
            "JOIN FETCH t.user u JOIN FETCH t.stock s WHERE t.id = :id")
    Optional<Transaction> findByIdWithDetails(@Param("id") Long id);
}
