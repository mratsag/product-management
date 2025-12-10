package com.wallet.payment_management.controller;

import com.wallet.payment_management.dto.request.WalletLedgerEntryFeeRequest;
import com.wallet.payment_management.dto.response.ErrorResponse;
import com.wallet.payment_management.dto.response.WalletLedgerEntryFeeResponse;
import com.wallet.payment_management.service.WalletLedgerEntryFeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/wallet-ledger-entry-fees")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Wallet Ledger Entry Fee", description = "Cüzdan hareketi ücretleri yönetimi API'leri")
public class WalletLedgerEntryFeeController {

    private final WalletLedgerEntryFeeService walletLedgerEntryFeeService;

    @PostMapping
    @Operation(summary = "Cüzdan hareketi ücreti oluştur", description = "Bir cüzdan hareketi için ücret kaydı oluşturur (WF-01)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ücret başarıyla oluşturuldu", content = @Content(schema = @Schema(implementation = WalletLedgerEntryFeeResponse.class))),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek verisi", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Cüzdan hareketi bulunamadı", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<WalletLedgerEntryFeeResponse> createFee(@Valid @RequestBody WalletLedgerEntryFeeRequest request) {
        log.info("REST request to create fee for ledger entry: {}", request.getWalletLedgerEntryId());
        WalletLedgerEntryFeeResponse response = walletLedgerEntryFeeService.createFee(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/ledger-entry/{ledgerEntryId}")
    @Operation(summary = "Cüzdan hareketi ücretlerini listele", description = "Belirtilen cüzdan hareketi için tüm ücretleri listeler (WF-02)")
    @ApiResponse(responseCode = "200", description = "Ücretler başarıyla getirildi", content = @Content(array = @ArraySchema(schema = @Schema(implementation = WalletLedgerEntryFeeResponse.class))))
    public ResponseEntity<List<WalletLedgerEntryFeeResponse>> getFeesByLedgerEntryId(
            @Parameter(description = "Cüzdan hareketi ID", required = true, example = "1") @PathVariable Long ledgerEntryId) {
        log.info("REST request to get fees for ledger entry: {}", ledgerEntryId);
        List<WalletLedgerEntryFeeResponse> responses = walletLedgerEntryFeeService.getFeesByLedgerEntryId(ledgerEntryId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/customer/{customerId}/report")
    @Operation(summary = "Müşteri ücret raporu", description = "Belirtilen tarih aralığında müşterinin ödediği toplam ücreti döner (WF-03)")
    @ApiResponse(responseCode = "200", description = "Rapor başarıyla oluşturuldu")
    public ResponseEntity<BigDecimal> getCustomerFeeReport(
            @Parameter(description = "Müşteri ID", required = true, example = "12345") @PathVariable Long customerId,
            @Parameter(description = "Başlangıç tarihi", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Bitiş tarihi", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("REST request to get fee report for customer: {}", customerId);
        BigDecimal total = walletLedgerEntryFeeService.getCustomerFeeReport(customerId, startDate, endDate);
        return ResponseEntity.ok(total);
    }
}
