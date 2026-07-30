package com.skala.stock.repository;

import com.skala.stock.dto.TransactionStatisticsDto;
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

    // 거래 내역 상세 (fetch join, 최신순)
    @Query("SELECT t FROM Transaction t JOIN FETCH t.user u JOIN FETCH t.stock s " +
            "WHERE u.id = :userId ORDER BY t.transactionDate DESC")
    List<Transaction> findByUserIdWithDetails(@Param("userId") Long userId);

    // 특정 주식 거래 내역 (fetch join, 최신순)
    @Query("SELECT t FROM Transaction t JOIN FETCH t.user u JOIN FETCH t.stock s " +
            "WHERE u.id = :userId AND s.id = :stockId ORDER BY t.transactionDate DESC")
    List<Transaction> findByUserIdAndStockIdWithDetails(@Param("userId") Long userId,
            @Param("stockId") Long stockId);

    @Query("SELECT new com.skala.stock.dto.TransactionStatisticsDto(" +
            "  s.id, s.code, s.name, " +
            "  SUM(CASE WHEN t.type = :buy THEN t.quantity ELSE 0 END), " +
            "  SUM(CASE WHEN t.type = :sell THEN t.quantity ELSE 0 END), " +
            "  SUM(CASE WHEN t.type = :buy THEN t.totalAmount ELSE 0 END), " +
            "  SUM(CASE WHEN t.type = :sell THEN t.totalAmount ELSE 0 END)) " +
            "FROM Transaction t JOIN t.stock s " +
            "WHERE t.user.id = :userId AND s.id = :stockId " +
            "GROUP BY s.id, s.code, s.name")
    
            Optional<TransactionStatisticsDto> getStatisticsByUserAndStock(
            @Param("userId") Long userId,
            @Param("stockId") Long stockId,
            @Param("buy") Transaction.TransactionType buy,
            @Param("sell") Transaction.TransactionType sell);
}
