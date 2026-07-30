package com.skala.stock.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockTransactionAnalysisDto {

    private Long userId;
    private Long stockId;
    private String stockCode;
    private String stockName;
    private List<TransactionDto> transactions;   // 거래 내역
    private TransactionStatisticsDto statistics; // 매수/매도 집계
}