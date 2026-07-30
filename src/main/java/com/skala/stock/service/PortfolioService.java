package com.skala.stock.service;

import com.skala.stock.dto.PortfolioDto;
import com.skala.stock.entity.Portfolio;
import com.skala.stock.entity.Stock;
import com.skala.stock.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;

    public List<PortfolioDto> getUserPortfolio(Long userId) {
        List<Portfolio> portfolios = portfolioRepository.findByUserId(userId);
        return portfolios.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 특정 주식 포트폴리오 조회
    public PortfolioDto getUserPortfolioByStock(Long userId, Long stockId) {
        Portfolio portfolio = portfolioRepository
                .findByUserIdAndStockIdWithDetails(userId, stockId)
                .orElseThrow(() -> new RuntimeException(
                        "해당 주식의 보유 내역이 없습니다. userId=" + userId + ", stockId=" + stockId));
        return convertToDto(portfolio);
    }

    private PortfolioDto convertToDto(Portfolio portfolio) {
        Stock stock = portfolio.getStock();
        Long currentPrice = stock.getCurrentPrice();
        Long totalValue = portfolio.getQuantity() * currentPrice;
        Long profitLoss = totalValue - (portfolio.getQuantity() * portfolio.getAveragePrice());

        return PortfolioDto.builder()
                .id(portfolio.getId())
                .userId(portfolio.getUser().getId())
                .username(portfolio.getUser().getUsername())
                .stockId(stock.getId())
                .stockCode(stock.getCode())
                .stockName(stock.getName())
                .quantity(portfolio.getQuantity())
                .averagePrice(portfolio.getAveragePrice())
                .currentPrice(currentPrice)
                .totalValue(totalValue)
                .profitLoss(profitLoss)
                .build();
    }
}
