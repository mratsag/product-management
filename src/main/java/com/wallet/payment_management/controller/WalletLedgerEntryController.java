package com.wallet.payment_management.controller;

import com.wallet.payment_management.dto.request.WalletLedgerEntryRequest;
import com.wallet.payment_management.dto.response.ErrorResponse;
import com.wallet.payment_management.dto.response.PageResponse;
import com.wallet.payment_management.dto.response.WalletLedgerEntryResponse;
import com.wallet.payment_management.enums.WalletLedgerEntryStatusEnum;
import com.wallet.payment_management.enums.WalletLedgerEntryTypeEnum;
import com.wallet.payment_management.service.WalletLedgerEntryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/wallet-ledger-entries")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Wallet Ledger Entry", description = "Cüzdan hareketleri yönetimi API'leri")
public class WalletLedgerEntryController {

    private final WalletLedgerEntryService walletLedgerEntryService;

    @PostMapping("/load")
    @Operation(summary = "Cüzdana bakiye yükle", description = "Cüzdana para yükler (WL-01)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Bakiye başarıyla yüklendi", content = @Content(schema = @Schema(implementation = WalletLedgerEntryResponse.class))),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek verisi veya kapalı hesap", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Cüzdan hesabı bulunamadı", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<WalletLedgerEntryResponse> createLoadEntry(@Valid @RequestBody WalletLedgerEntryRequest request) {
        log.info("REST request to create LOAD entry for wallet account: {}", request.getWalletAccountId());
        WalletLedgerEntryResponse response = walletLedgerEntryService.createLoadEntry(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/spend")
    @Operation(summary = "Cüzdandan harcama yap", description = "Cüzdandan para harcar (WL-02)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Harcama başarıyla yapıldı", content = @Content(schema = @Schema(implementation = WalletLedgerEntryResponse.class))),
            @ApiResponse(responseCode = "400", description = "Yetersiz bakiye veya geçersiz istek", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Cüzdan hesabı bulunamadı", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<WalletLedgerEntryResponse> createSpendEntry(@Valid @RequestBody WalletLedgerEntryRequest request) {
        log.info("REST request to create SPEND entry for wallet account: {}", request.getWalletAccountId());
        WalletLedgerEntryResponse response = walletLedgerEntryService.createSpendEntry(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/refund")
    @Operation(summary = "Cüzdana iade yap", description = "Cüzdana iade yapar (WL-03)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "İade başarıyla yapıldı", content = @Content(schema = @Schema(implementation = WalletLedgerEntryResponse.class))),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek verisi", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Cüzdan hesabı bulunamadı", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<WalletLedgerEntryResponse> createRefundEntry(@Valid @RequestBody WalletLedgerEntryRequest request) {
        log.info("REST request to create REFUND entry for wallet account: {}", request.getWalletAccountId());
        WalletLedgerEntryResponse response = walletLedgerEntryService.createRefundEntry(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/adjustment")
    @Operation(summary = "Manuel düzeltme yap", description = "Cüzdan için manuel düzeltme yapar (WL-04)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Düzeltme başarıyla yapıldı", content = @Content(schema = @Schema(implementation = WalletLedgerEntryResponse.class))),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek verisi", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Cüzdan hesabı bulunamadı", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<WalletLedgerEntryResponse> createAdjustmentEntry(@Valid @RequestBody WalletLedgerEntryRequest request) {
        log.info("REST request to create ADJUSTMENT entry for wallet account: {}", request.getWalletAccountId());
        WalletLedgerEntryResponse response = walletLedgerEntryService.createAdjustmentEntry(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Cüzdan hareketlerini listele", description = "Filtreleme ile cüzdan hareketlerini listeler (WL-05). Pagination desteği: ?page=0&size=20&sort=createdAt,desc")
    @ApiResponse(responseCode = "200", description = "Hareketler başarıyla getirildi", content = @Content(schema = @Schema(implementation = PageResponse.class)))
    public ResponseEntity<PageResponse<WalletLedgerEntryResponse>> getLedgerEntries(
            @Parameter(description = "Cüzdan hesabı ID") @RequestParam(required = false) Long walletAccountId,
            @Parameter(description = "Müşteri ID") @RequestParam(required = false) Long customerId,
            @Parameter(description = "Hareket tipi") @RequestParam(required = false) WalletLedgerEntryTypeEnum entryType,
            @Parameter(description = "Durum") @RequestParam(required = false) WalletLedgerEntryStatusEnum status,
            @Parameter(description = "Başlangıç tarihi") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Bitiş tarihi") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        log.info("REST request to get ledger entries");
        PageResponse<WalletLedgerEntryResponse> response = walletLedgerEntryService.getLedgerEntries(
                walletAccountId, customerId, entryType, status, startDate, endDate, pageable);
        return ResponseEntity.ok(response);
    }
}
