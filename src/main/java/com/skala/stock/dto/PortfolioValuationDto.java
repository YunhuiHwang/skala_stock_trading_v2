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
public class PortfolioValuationDto {

    private Long userId;
    private String username;
    private Long totalInvestment;             // 매수원금 합계
    private Long totalEvaluationValue;        // 평가금액 합계
    private Long totalProfitLoss;             // 총 평가손익
    private List<PortfolioDto> holdings;      // 종목별 평가손익
}