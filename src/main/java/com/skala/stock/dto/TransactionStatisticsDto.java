package com.skala.stock.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionStatisticsDto {
    private Long stockId; // 분석용 추가
    private String stockCode;
    private String stockName;
    private Long totalBuyQuantity;
    private Long totalSellQuantity;
    private Long netQuantity;
    private Long totalBuyAmount;
    private Long totalSellAmount;
    private Long netAmount;

    public TransactionStatisticsDto(Long stockId, String stockCode, String stockName,
                                    Long totalBuyQuantity, Long totalSellQuantity,
                                    Long totalBuyAmount, Long totalSellAmount) {
        this.stockId = stockId;
        this.stockCode = stockCode;
        this.stockName = stockName;
        this.totalBuyQuantity = totalBuyQuantity == null ? 0L : totalBuyQuantity;
        this.totalSellQuantity = totalSellQuantity == null ? 0L : totalSellQuantity;
        this.totalBuyAmount = totalBuyAmount == null ? 0L : totalBuyAmount;
        this.totalSellAmount = totalSellAmount == null ? 0L : totalSellAmount;
        this.netQuantity = this.totalBuyQuantity - this.totalSellQuantity;
        this.netAmount = this.totalBuyAmount - this.totalSellAmount;
    }
}


