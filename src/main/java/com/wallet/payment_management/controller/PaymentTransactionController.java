package com.wallet.payment_management.controller;

import com.wallet.payment_management.dto.request.PaymentTransactionRequest;
import com.wallet.payment_management.dto.response.ErrorResponse;
import com.wallet.payment_management.dto.response.PaymentTransactionResponse;
import com.wallet.payment_management.enums.PaymentTransactionStatusEnum;
import com.wallet.payment_management.service.PaymentTransactionService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment-transactions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Payment Transaction", description = "Ödeme işlemleri yönetimi API'leri")
public class PaymentTransactionController {

    private final PaymentTransactionService paymentTransactionService;

    @PostMapping
    @Operation(summary = "Ödeme işlemi oluştur", description = "Yeni bir ödeme işlemi oluşturur (PT-01, PT-02)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "İşlem başarıyla oluşturuldu", content = @Content(schema = @Schema(implementation = PaymentTransactionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek verisi", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Ödeme bulunamadı", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PaymentTransactionResponse> createTransaction(@Valid @RequestBody PaymentTransactionRequest request) {
        log.info("REST request to create payment transaction. Payment ID: {}", request.getPaymentId());
        PaymentTransactionResponse response = paymentTransactionService.createTransaction(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "İşlem sonucunu işle", description = "Ödeme işleminin sonucunu işler (PT-03)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "İşlem sonucu başarıyla işlendi", content = @Content(schema = @Schema(implementation = PaymentTransactionResponse.class))),
            @ApiResponse(responseCode = "404", description = "İşlem bulunamadı", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PaymentTransactionResponse> processTransactionResult(
            @Parameter(description = "İşlem ID", required = true, example = "1") @PathVariable Long id,
            @Parameter(description = "İşlem durumu", required = true) @RequestParam PaymentTransactionStatusEnum status) {
        log.info("REST request to process transaction result. ID: {}, Status: {}", id, status);
        PaymentTransactionResponse response = paymentTransactionService.processTransactionResult(id, status);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/payment/{paymentId}")
    @Operation(summary = "Ödeme işlemlerini listele", description = "Belirtilen ödeme için tüm işlemleri listeler (PT-04)")
    @ApiResponse(responseCode = "200", description = "İşlemler başarıyla getirildi", content = @Content(array = @ArraySchema(schema = @Schema(implementation = PaymentTransactionResponse.class))))
    public ResponseEntity<List<PaymentTransactionResponse>> getTransactionsByPaymentId(
            @Parameter(description = "Ödeme ID", required = true, example = "1") @PathVariable Long paymentId) {
        log.info("REST request to get transactions for payment: {}", paymentId);
        List<PaymentTransactionResponse> responses = paymentTransactionService.getTransactionsByPaymentId(paymentId);
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{id}/correlate")
    @Operation(summary = "Wallet ile ilişkilendir", description = "Ödeme işlemini wallet hareketi ile ilişkilendirir (PT-05)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "İlişkilendirme başarılı"),
            @ApiResponse(responseCode = "404", description = "İşlem bulunamadı", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> correlateWithWallet(
            @Parameter(description = "İşlem ID", required = true, example = "1") @PathVariable Long id,
            @Parameter(description = "Wallet referans numarası", required = true) @RequestParam String walletReference) {
        log.info("REST request to correlate transaction {} with wallet reference: {}", id, walletReference);
        paymentTransactionService.correlateWithWallet(id, walletReference);
        return ResponseEntity.ok().build();
    }
}
