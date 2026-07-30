package com.skala.stock.service;

import com.skala.stock.dto.TradeRequestDto;
import com.skala.stock.dto.TransactionDto;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final StockRepository stockRepository;
    private final PortfolioRepository portfolioRepository;

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<TransactionDto> getUserTransactions(Long userId) {
        List<Transaction> transactions = transactionRepository.findByUserIdOrderByTransactionDateDesc(userId);
        return transactions.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private TransactionDto convertToDto(Transaction transaction) {
        return TransactionDto.builder()
                .id(transaction.getId())
                .userId(transaction.getUser().getId())
                .username(transaction.getUser().getUsername())
                .stockId(transaction.getStock().getId())
                .stockCode(transaction.getStock().getCode())
                .stockName(transaction.getStock().getName())
                .type(transaction.getType())
                .quantity(transaction.getQuantity())
                .price(transaction.getPrice())
                .totalAmount(transaction.getTotalAmount())
                .transactionDate(transaction.getTransactionDate())
                .createdAt(transaction.getCreatedAt())
                .build();
    }

    // 거래 상세 조회
    public TransactionDto getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("거래 내역을 찾을 수 없습니다: " + id));
        return convertToDto(transaction);
    }

    // 주식 매매 실행
    @Transactional
    public TransactionDto executeTrade(TradeRequestDto request) {
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new RuntimeException("거래 수량은 1 이상이어야 합니다.");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + request.getUserId()));
        Stock stock = stockRepository.findById(request.getStockId())
                .orElseThrow(() -> new RuntimeException("주식을 찾을 수 없습니다: " + request.getStockId()));

        long price = stock.getCurrentPrice();
        long quantity = request.getQuantity();
        long totalAmount = price * quantity;

        if (request.getType() == Transaction.TransactionType.BUY) {
            processBuy(user, stock, quantity, price, totalAmount);
        } else if (request.getType() == Transaction.TransactionType.SELL) {
            processSell(user, stock, quantity, totalAmount);
        } else {
            throw new RuntimeException("거래 유형이 올바르지 않습니다.");
        }

        userRepository.save(user);

        Transaction transaction = Transaction.builder()
                .user(user).stock(stock)
                .type(request.getType())
                .quantity(quantity).price(price).totalAmount(totalAmount)
                .build();
        Transaction saved = transactionRepository.save(transaction);

        return convertToDto(saved);
    }

    private void processBuy(User user, Stock stock, long quantity, long price, long totalAmount) {
        if (user.getBalance() < totalAmount) {
            throw new RuntimeException("잔액이 부족합니다. 필요: " + totalAmount + ", 보유: " + user.getBalance());
        }
        user.setBalance(user.getBalance() - totalAmount);

        Portfolio portfolio = portfolioRepository
                .findByUserIdAndStockId(user.getId(), stock.getId())
                .orElse(null);

        if (portfolio == null) {
            portfolio = Portfolio.builder()
                    .user(user).stock(stock)
                    .quantity(quantity).averagePrice(price)
                    .build();
        } else {
            long newQuantity = portfolio.getQuantity() + quantity;
            long newAveragePrice = (portfolio.getQuantity() * portfolio.getAveragePrice() + totalAmount) / newQuantity;
            portfolio.setQuantity(newQuantity);
            portfolio.setAveragePrice(newAveragePrice);
        }
        portfolioRepository.save(portfolio);
    }

    private void processSell(User user, Stock stock, long quantity, long totalAmount) {
        Portfolio portfolio = portfolioRepository
                .findByUserIdAndStockId(user.getId(), stock.getId())
                .orElseThrow(() -> new RuntimeException("보유하지 않은 주식은 매도할 수 없습니다: " + stock.getCode()));

        if (portfolio.getQuantity() < quantity) {
            throw new RuntimeException("보유 수량 부족. 보유: " + portfolio.getQuantity() + ", 요청: " + quantity);
        }

        user.setBalance(user.getBalance() + totalAmount);

        long remaining = portfolio.getQuantity() - quantity;
        if (remaining == 0) {
            portfolioRepository.delete(portfolio);
        } else {
            portfolio.setQuantity(remaining); // 평균가 유지
            portfolioRepository.save(portfolio);
        }
    }
}
