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
public class ReturnRateDto {

    private Long userId;
    private String username;
    private Long totalInvestment;   // 매수원금
    private Long evaluationValue;   // 평가금액
    private Long profitLoss;        // 평가손익
    private Double returnRate;      // 수익률(%)
}