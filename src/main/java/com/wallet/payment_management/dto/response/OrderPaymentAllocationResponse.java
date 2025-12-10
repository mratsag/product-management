package com.wallet.payment_management.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Sipariş ödeme tahsisi yanıtı")
public class OrderPaymentAllocationResponse {

    @Schema(description = "Tahsis ID", example = "1")
    private Long id;

    @Schema(description = "Sipariş ID (external reference)", example = "1001")
    private Long orderId;

    @Schema(description = "Ödeme ID", example = "1")
    private Long paymentId;

    @Schema(description = "Tahsis edilen tutar", example = "250.00")
    private BigDecimal allocatedAmount;

    @Schema(description = "Oluşturulma tarihi", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;
}
