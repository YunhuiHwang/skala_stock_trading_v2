package com.skala.stock.repository;

import com.skala.stock.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    List<Portfolio> findByUserId(Long userId);

    Optional<Portfolio> findByUserIdAndStockId(Long userId, Long stockId);

    boolean existsByUserIdAndStockId(Long userId, Long stockId);

    // 특정 주식 포트폴리오 조회 (user, stock 같이 로딩)
    @Query("SELECT p FROM Portfolio p " +
            "JOIN FETCH p.user u JOIN FETCH p.stock s " +
            "WHERE u.id = :userId AND s.id = :stockId")
    Optional<Portfolio> findByUserIdAndStockIdWithDetails(@Param("userId") Long userId,
            @Param("stockId") Long stockId);
}
