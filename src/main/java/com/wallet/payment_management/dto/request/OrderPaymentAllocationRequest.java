package com.wallet.payment_management.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Sipariş ödeme tahsisi oluşturma isteği")
public class OrderPaymentAllocationRequest {

    @NotNull(message = "Order ID is required")
    @Schema(description = "Sipariş ID (external reference)", example = "1001", required = true)
    private Long orderId;

    @NotNull(message = "Payment ID is required")
    @Schema(description = "Ödeme ID", example = "1", required = true)
    private Long paymentId;

    @NotNull(message = "Allocated amount is required")
    @DecimalMin(value = "0.01", message = "Allocated amount must be greater than 0")
    @Schema(description = "Tahsis edilen tutar", example = "250.00", required = true)
    private BigDecimal allocatedAmount;
}
