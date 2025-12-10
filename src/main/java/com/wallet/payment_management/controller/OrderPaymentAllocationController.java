package com.wallet.payment_management.controller;

import com.wallet.payment_management.dto.request.OrderPaymentAllocationRequest;
import com.wallet.payment_management.dto.response.ErrorResponse;
import com.wallet.payment_management.dto.response.OrderPaymentAllocationResponse;
import com.wallet.payment_management.service.OrderPaymentAllocationService;
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
@RequestMapping("/api/order-payment-allocations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Order Payment Allocation", description = "Sipariş ödeme tahsisi yönetimi API'leri")
public class OrderPaymentAllocationController {

    private final OrderPaymentAllocationService orderPaymentAllocationService;

    @PostMapping
    @Operation(summary = "Sipariş ödeme tahsisi oluştur", description = "Bir ödemeyi sipariş ile ilişkilendirir (OPA-01)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tahsis başarıyla oluşturuldu", content = @Content(schema = @Schema(implementation = OrderPaymentAllocationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek verisi", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Ödeme bulunamadı", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<OrderPaymentAllocationResponse> createAllocation(@Valid @RequestBody OrderPaymentAllocationRequest request) {
        log.info("REST request to create allocation. Order ID: {}, Payment ID: {}", request.getOrderId(), request.getPaymentId());
        OrderPaymentAllocationResponse response = orderPaymentAllocationService.createAllocation(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Sipariş tahsislerini listele", description = "Belirtilen sipariş için tüm tahsisleri listeler (OPA-02)")
    @ApiResponse(responseCode = "200", description = "Tahsisler başarıyla getirildi", content = @Content(array = @ArraySchema(schema = @Schema(implementation = OrderPaymentAllocationResponse.class))))
    public ResponseEntity<List<OrderPaymentAllocationResponse>> getAllocationsByOrderId(
            @Parameter(description = "Sipariş ID", required = true, example = "1001") @PathVariable Long orderId) {
        log.info("REST request to get allocations for order: {}", orderId);
        List<OrderPaymentAllocationResponse> responses = orderPaymentAllocationService.getAllocationsByOrderId(orderId);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Tahsis yeniden düzenle", description = "Mevcut tahsisi yeniden düzenler (OPA-03)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tahsis başarıyla güncellendi", content = @Content(schema = @Schema(implementation = OrderPaymentAllocationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek verisi", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Tahsis bulunamadı", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<OrderPaymentAllocationResponse> reallocate(
            @Parameter(description = "Tahsis ID", required = true, example = "1") @PathVariable Long id,
            @Valid @RequestBody OrderPaymentAllocationRequest request) {
        log.info("REST request to reallocate. ID: {}", id);
        OrderPaymentAllocationResponse response = orderPaymentAllocationService.reallocate(id, request);
        return ResponseEntity.ok(response);
    }
}
