package com.wallet.payment_management.controller;

import com.wallet.payment_management.dto.request.WalletAccountRequest;
import com.wallet.payment_management.dto.request.WalletAccountStatusUpdateRequest;
import com.wallet.payment_management.dto.response.ErrorResponse;
import com.wallet.payment_management.dto.response.PageResponse;
import com.wallet.payment_management.dto.response.WalletAccountResponse;
import com.wallet.payment_management.service.WalletAccountService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/wallet-accounts")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Wallet Account", description = "Cüzdan hesabı yönetimi API'leri")
public class WalletAccountController {

        private final WalletAccountService walletAccountService;

        @PostMapping
        @Operation(summary = "Yeni cüzdan hesabı oluştur", description = "Müşteri için yeni bir cüzdan hesabı oluşturur (WA-01)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Cüzdan hesabı başarıyla oluşturuldu", content = @Content(schema = @Schema(implementation = WalletAccountResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Geçersiz istek verisi", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
        public ResponseEntity<WalletAccountResponse> createWalletAccount(
                        @Valid @RequestBody WalletAccountRequest request) {
                log.info("REST request to create wallet account for customer: {}", request.getCustomerId());
                WalletAccountResponse response = walletAccountService.createWalletAccount(request);
                return new ResponseEntity<>(response, HttpStatus.CREATED);
        }

        @GetMapping("/customer/{customerId}")
        @Operation(summary = "Müşteri cüzdan hesaplarını listele", description = "Belirtilen müşteriye ait tüm cüzdan hesaplarını listeler (WA-02). Pagination desteği: ?page=0&size=20&sort=createdAt,desc")
        @ApiResponse(responseCode = "200", description = "Cüzdan hesapları başarıyla getirildi", content = @Content(schema = @Schema(implementation = PageResponse.class)))
        public ResponseEntity<PageResponse<WalletAccountResponse>> getWalletAccountsByCustomerId(
                        @Parameter(description = "Müşteri ID", required = true, example = "12345") @PathVariable Long customerId,
                        @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
                log.info("REST request to get wallet accounts for customer: {}, Page: {}, Size: {}",
                                customerId, pageable.getPageNumber(), pageable.getPageSize());
                PageResponse<WalletAccountResponse> response = walletAccountService
                                .getWalletAccountsByCustomerId(customerId, pageable);
                return ResponseEntity.ok(response);
        }

        @GetMapping("/{id}")
        @Operation(summary = "Cüzdan hesabı detayı", description = "ID ile cüzdan hesabı detaylarını getirir (WA-03)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Cüzdan hesabı bulundu", content = @Content(schema = @Schema(implementation = WalletAccountResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Cüzdan hesabı bulunamadı", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
        public ResponseEntity<WalletAccountResponse> getWalletAccountById(
                        @Parameter(description = "Cüzdan hesabı ID", required = true, example = "1") @PathVariable Long id) {
                log.info("REST request to get wallet account by id: {}", id);
                WalletAccountResponse response = walletAccountService.getWalletAccountById(id);
                return ResponseEntity.ok(response);
        }

        @PatchMapping("/{id}/status")
        @Operation(summary = "Cüzdan hesabı durumunu güncelle", description = "Cüzdan hesabının durumunu günceller (WA-04)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Durum başarıyla güncellendi", content = @Content(schema = @Schema(implementation = WalletAccountResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Geçersiz durum geçişi", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Cüzdan hesabı bulunamadı", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
        public ResponseEntity<WalletAccountResponse> updateWalletAccountStatus(
                        @Parameter(description = "Cüzdan hesabı ID", required = true, example = "1") @PathVariable Long id,
                        @Valid @RequestBody WalletAccountStatusUpdateRequest request) {
                log.info("REST request to update wallet account status. ID: {}, Status: {}", id, request.getStatus());
                WalletAccountResponse response = walletAccountService.updateWalletAccountStatus(id, request);
                return ResponseEntity.ok(response);
        }

        @GetMapping("/{id}/check-balance")
        @Operation(summary = "Bakiye kontrolü", description = "Cüzdan hesabında yeterli bakiye olup olmadığını kontrol eder (WA-05)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Bakiye kontrolü yapıldı"),
                        @ApiResponse(responseCode = "400", description = "Yetersiz bakiye veya kapalı hesap", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Cüzdan hesabı bulunamadı", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
        public ResponseEntity<Boolean> checkBalance(
                        @Parameter(description = "Cüzdan hesabı ID", required = true, example = "1") @PathVariable Long id,
                        @Parameter(description = "Gerekli tutar", required = true, example = "100.00") @RequestParam BigDecimal requiredAmount) {
                log.info("REST request to check balance. ID: {}, Required: {}", id, requiredAmount);
                boolean hasBalance = walletAccountService.checkBalance(id, requiredAmount);
                return ResponseEntity.ok(hasBalance);
        }

        @GetMapping
        @Operation(summary = "Tüm cüzdan hesaplarını listele", description = "Tüm cüzdan hesaplarını pagination ile listeler (WA-06)")
        @ApiResponse(responseCode = "200", description = "Cüzdan hesapları başarıyla getirildi", content = @Content(schema = @Schema(implementation = PageResponse.class)))
        public ResponseEntity<PageResponse<WalletAccountResponse>> getAllWalletAccounts(
                        @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
                log.info("REST request to get all wallet accounts. Page: {}, Size: {}",
                                pageable.getPageNumber(), pageable.getPageSize());
                PageResponse<WalletAccountResponse> response = walletAccountService.getAllWalletAccounts(pageable);
                return ResponseEntity.ok(response);
        }
}
