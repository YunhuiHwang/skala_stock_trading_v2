package com.skala.stock.controller;

import com.skala.stock.dto.AssetSummaryDto;
import com.skala.stock.dto.PortfolioValuationDto;
import com.skala.stock.dto.ReturnRateDto;
import com.skala.stock.dto.StockTransactionAnalysisDto;
import com.skala.stock.dto.TransactionDto;
import com.skala.stock.service.AnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
@Tag(name = "분석 기능", description = "포트폴리오·자산·수익률 분석 API")
public class AnalysisController {

    private final AnalysisService analysisService;

    // 포트폴리오 평가 손익 조회
    @GetMapping("/portfolio/{userId}/profit-loss")
    @Operation(summary = "포트폴리오 평가 손익 조회")
    public ResponseEntity<PortfolioValuationDto> getPortfolioValuation(@PathVariable Long userId) {
        return ResponseEntity.ok(analysisService.getPortfolioValuation(userId));
    }

    // 거래 내역 상세 조회
    @GetMapping("/transactions/{userId}")
    @Operation(summary = "거래 내역 상세 조회")
    public ResponseEntity<List<TransactionDto>> getTransactionHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(analysisService.getTransactionHistory(userId));
    }

    // 특정 주식 거래 내역 조회
    @GetMapping("/transactions/{userId}/stock/{stockId}")
    @Operation(summary = "특정 주식 거래 내역 조회")
    public ResponseEntity<StockTransactionAnalysisDto> getStockTransactionAnalysis(
            @PathVariable Long userId, @PathVariable Long stockId) {
        return ResponseEntity.ok(analysisService.getStockTransactionAnalysis(userId, stockId));
    }

    // 총 자산 조회
    @GetMapping("/assets/{userId}")
    @Operation(summary = "총 자산 조회")
    public ResponseEntity<AssetSummaryDto> getTotalAssets(@PathVariable Long userId) {
        return ResponseEntity.ok(analysisService.getTotalAssets(userId));
    }

    // 총 수익률 조회
    @GetMapping("/return-rate/{userId}")
    @Operation(summary = "총 수익률 조회")
    public ResponseEntity<ReturnRateDto> getTotalReturnRate(@PathVariable Long userId) {
        return ResponseEntity.ok(analysisService.getTotalReturnRate(userId));
    }
}