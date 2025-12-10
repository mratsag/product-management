package com.wallet.payment_management.controller;

import com.wallet.payment_management.dto.request.PaymentTransactionFeeRequest;
import com.wallet.payment_management.dto.response.ErrorResponse;
import com.wallet.payment_management.dto.response.PaymentTransactionFeeResponse;
import com.wallet.payment_management.service.PaymentTransactionFeeService;
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

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/payment-transaction-fees")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Payment Transaction Fee", description = "Ödeme işlemi ücretleri yönetimi API'leri")
public class PaymentTransactionFeeController {

    private final PaymentTransactionFeeService paymentTransactionFeeService;

    @PostMapping
    @Operation(summary = "Ödeme işlemi ücreti oluştur", description = "Bir ödeme işlemi için ücret kaydı oluşturur (PF-01)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ücret başarıyla oluşturuldu", content = @Content(schema = @Schema(implementation = PaymentTransactionFeeResponse.class))),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek verisi", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Ödeme işlemi bulunamadı", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PaymentTransactionFeeResponse> createFee(@Valid @RequestBody PaymentTransactionFeeRequest request) {
        log.info("REST request to create fee for payment transaction: {}", request.getPaymentTransactionId());
        PaymentTransactionFeeResponse response = paymentTransactionFeeService.createFee(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/transaction/{transactionId}")
    @Operation(summary = "Ödeme işlemi ücretlerini listele", description = "Belirtilen ödeme işlemi için tüm ücretleri listeler (PF-02)")
    @ApiResponse(responseCode = "200", description = "Ücretler başarıyla getirildi", content = @Content(array = @ArraySchema(schema = @Schema(implementation = PaymentTransactionFeeResponse.class))))
    public ResponseEntity<List<PaymentTransactionFeeResponse>> getFeesByTransactionId(
            @Parameter(description = "Ödeme işlemi ID", required = true, example = "1") @PathVariable Long transactionId) {
        log.info("REST request to get fees for payment transaction: {}", transactionId);
        List<PaymentTransactionFeeResponse> responses = paymentTransactionFeeService.getFeesByTransactionId(transactionId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/payment/{paymentId}/total")
    @Operation(summary = "Ödeme toplam ücreti", description = "Belirtilen ödeme için toplam ücret tutarını döner (PF-03)")
    @ApiResponse(responseCode = "200", description = "Toplam ücret başarıyla hesaplandı")
    public ResponseEntity<BigDecimal> getTotalFeesByPaymentId(
            @Parameter(description = "Ödeme ID", required = true, example = "1") @PathVariable Long paymentId) {
        log.info("REST request to get total fees for payment: {}", paymentId);
        BigDecimal total = paymentTransactionFeeService.getTotalFeesByPaymentId(paymentId);
        return ResponseEntity.ok(total);
    }
}
