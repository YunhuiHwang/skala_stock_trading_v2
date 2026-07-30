package com.skala.stock.service;

import com.skala.stock.dto.AssetSummaryDto;
import com.skala.stock.dto.PortfolioDto;
import com.skala.stock.dto.PortfolioValuationDto;
import com.skala.stock.dto.ReturnRateDto;
import com.skala.stock.dto.StockTransactionAnalysisDto;
import com.skala.stock.dto.TransactionDto;
import com.skala.stock.dto.TransactionStatisticsDto;
import com.skala.stock.entity.Portfolio;
import com.skala.stock.entity.Stock;
import com.skala.stock.entity.Transaction;
import com.skala.stock.entity.User;
import com.skala.stock.repository.PortfolioRepository;
import com.skala.stock.repository.StockRepository;
import com.skala.stock.repository.TransactionRepository;
import com.skala.stock.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalysisService {

    private final UserRepository userRepository;
    private final StockRepository stockRepository;
    private final PortfolioRepository portfolioRepository;
    private final TransactionRepository transactionRepository;

    // 포트폴리오 평가 손익
    public PortfolioValuationDto getPortfolioValuation(Long userId) {
        User user = getUser(userId);
        List<PortfolioDto> holdings = portfolioRepository.findByUserIdWithDetails(userId).stream()
                .map(this::toPortfolioDto)
                .toList();

        long investment = holdings.stream().mapToLong(h -> h.getQuantity() * h.getAveragePrice()).sum();
        long evaluation = holdings.stream().mapToLong(PortfolioDto::getTotalValue).sum();

        return PortfolioValuationDto.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .holdings(holdings)
                .totalInvestment(investment)
                .totalEvaluationValue(evaluation)
                .totalProfitLoss(evaluation - investment)
                .build();
    }

    // 거래 내역 상세
    public List<TransactionDto> getTransactionHistory(Long userId) {
        getUser(userId);
        return transactionRepository.findByUserIdWithDetails(userId).stream()
                .map(this::toTransactionDto)
                .toList();
    }

    // 특정 주식 거래 내역 (+통계)
    public StockTransactionAnalysisDto getStockTransactionAnalysis(Long userId, Long stockId) {
        getUser(userId);
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new RuntimeException("주식을 찾을 수 없습니다: " + stockId));

        List<TransactionDto> txs = transactionRepository
                .findByUserIdAndStockIdWithDetails(userId, stockId).stream()
                .map(this::toTransactionDto)
                .toList();

        TransactionStatisticsDto stats = transactionRepository
                .getStatisticsByUserAndStock(userId, stockId,
                        Transaction.TransactionType.BUY,
                        Transaction.TransactionType.SELL)
                .orElseGet(() -> new TransactionStatisticsDto(
                        stock.getId(), stock.getCode(), stock.getName(), 0L, 0L, 0L, 0L));

        return StockTransactionAnalysisDto.builder()
                .userId(userId)
                .stockId(stock.getId())
                .stockCode(stock.getCode())
                .stockName(stock.getName())
                .transactions(txs)
                .statistics(stats)
                .build();
    }

    // 총 자산
    public AssetSummaryDto getTotalAssets(Long userId) {
        User user = getUser(userId);
        long evaluation = nz(portfolioRepository.getTotalEvaluationValue(userId));
        return AssetSummaryDto.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .balance(user.getBalance())
                .evaluationValue(evaluation)
                .totalAssets(user.getBalance() + evaluation)
                .build();
    }

    // 총 수익률
    public ReturnRateDto getTotalReturnRate(Long userId) {
        User user = getUser(userId);
        long investment = nz(portfolioRepository.getTotalInvestment(userId));
        long evaluation = nz(portfolioRepository.getTotalEvaluationValue(userId));
        return ReturnRateDto.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .totalInvestment(investment)
                .evaluationValue(evaluation)
                .profitLoss(evaluation - investment)
                .returnRate(rate(evaluation, investment))
                .build();
    }

    // helpers
    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + id));
    }

    private long nz(Long v) {
        return v == null ? 0L : v;
    }

    // 수익률(%) = (평가금액 - 매수원금) / 매수원금 × 100, 소수 둘째자리
    private double rate(long eval, long invest) {
        if (invest == 0)
            return 0.0;
        return Math.round((eval - invest) * 10000.0 / invest) / 100.0;
    }

    private PortfolioDto toPortfolioDto(Portfolio p) {
        Stock s = p.getStock();
        long totalValue = p.getQuantity() * s.getCurrentPrice();
        long profitLoss = totalValue - (p.getQuantity() * p.getAveragePrice());
        return PortfolioDto.builder()
                .id(p.getId())
                .userId(p.getUser().getId())
                .username(p.getUser().getUsername())
                .stockId(s.getId())
                .stockCode(s.getCode())
                .stockName(s.getName())
                .quantity(p.getQuantity())
                .averagePrice(p.getAveragePrice())
                .currentPrice(s.getCurrentPrice())
                .totalValue(totalValue)
                .profitLoss(profitLoss)
                .build();
    }

    private TransactionDto toTransactionDto(Transaction t) {
        return TransactionDto.builder()
                .id(t.getId())
                .userId(t.getUser().getId())
                .username(t.getUser().getUsername())
                .stockId(t.getStock().getId())
                .stockCode(t.getStock().getCode())
                .stockName(t.getStock().getName())
                .type(t.getType())
                .quantity(t.getQuantity())
                .price(t.getPrice())
                .totalAmount(t.getTotalAmount())
                .transactionDate(t.getTransactionDate())
                .createdAt(t.getCreatedAt())
                .build();
    }
}