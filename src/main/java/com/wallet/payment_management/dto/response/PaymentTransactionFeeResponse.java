package com.wallet.payment_management.dto.response;

import com.wallet.payment_management.enums.PaymentFeeTypeEnum;
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
@Schema(description = "Ödeme işlemi ücreti yanıtı")
public class PaymentTransactionFeeResponse {

    @Schema(description = "Ücret ID", example = "1")
    private Long id;

    @Schema(description = "Ödeme işlemi ID", example = "1")
    private Long paymentTransactionId;

    @Schema(description = "Ücret tipi", example = "GATEWAY_FEE")
    private PaymentFeeTypeEnum feeType;

    @Schema(description = "Ücret tutarı", example = "2.50")
    private BigDecimal amount;

    @Schema(description = "Açıklama", example = "Gateway ücreti")
    private String description;

    @Schema(description = "Oluşturulma tarihi", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;
}
