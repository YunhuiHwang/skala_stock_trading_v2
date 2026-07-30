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
public class AssetSummaryDto {

    private Long userId;            // 사용자 ID
    private String username;        // 사용자명
    private Long balance;           // 보유 현금
    private Long evaluationValue;   // 주식 평가금액 = Σ(보유수량 × 현재가)
    private Long totalAssets;       // 총 자산 = 현금 + 평가금액
}