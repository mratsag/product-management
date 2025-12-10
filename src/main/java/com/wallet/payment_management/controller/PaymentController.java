package com.wallet.payment_management.controller;

import com.wallet.payment_management.dto.request.PaymentRequest;
import com.wallet.payment_management.dto.response.ErrorResponse;
import com.wallet.payment_management.dto.response.PageResponse;
import com.wallet.payment_management.dto.response.PaymentResponse;
import com.wallet.payment_management.enums.PaymentStatusEnum;
import com.wallet.payment_management.enums.PaymentTypeEnum;
import com.wallet.payment_management.service.PaymentService;
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
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Payment", description = "Ödeme yönetimi API'leri")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @Operation(summary = "Yeni ödeme oluştur", description = "Yeni bir ödeme kaydı oluşturur (P-01, P-02)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ödeme başarıyla oluşturuldu", content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek verisi", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody PaymentRequest request) {
        log.info("REST request to create payment. Type: {}, Amount: {}", request.getPaymentType(), request.getAmount());
        PaymentResponse response = paymentService.createPayment(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Ödeme durumunu güncelle", description = "Ödeme durumunu günceller (P-03)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Durum başarıyla güncellendi", content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
            @ApiResponse(responseCode = "404", description = "Ödeme bulunamadı", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PaymentResponse> updatePaymentStatus(
            @Parameter(description = "Ödeme ID", required = true, example = "1") @PathVariable Long id,
            @Parameter(description = "Yeni durum", required = true) @RequestParam PaymentStatusEnum status) {
        log.info("REST request to update payment status. ID: {}, Status: {}", id, status);
        PaymentResponse response = paymentService.updatePaymentStatus(id, status);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Ödemeleri listele", description = "Filtreleme ile ödemeleri listeler (P-04). Pagination desteği: ?page=0&size=20&sort=createdAt,desc")
    @ApiResponse(responseCode = "200", description = "Ödemeler başarıyla getirildi", content = @Content(schema = @Schema(implementation = PageResponse.class)))
    public ResponseEntity<PageResponse<PaymentResponse>> getPayments(
            @Parameter(description = "Ödeme tipi") @RequestParam(required = false) PaymentTypeEnum paymentType,
            @Parameter(description = "Durum") @RequestParam(required = false) PaymentStatusEnum status,
            @Parameter(description = "Başlangıç tarihi") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Bitiş tarihi") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        log.info("REST request to get payments");
        PageResponse<PaymentResponse> response = paymentService.getPayments(paymentType, status, startDate, endDate, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Ödeme detayı", description = "ID ile ödeme detaylarını getirir")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ödeme bulundu", content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
            @ApiResponse(responseCode = "404", description = "Ödeme bulunamadı", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PaymentResponse> getPaymentById(
            @Parameter(description = "Ödeme ID", required = true, example = "1") @PathVariable Long id) {
        log.info("REST request to get payment by id: {}", id);
        PaymentResponse response = paymentService.getPaymentById(id);
        return ResponseEntity.ok(response);
    }
}
