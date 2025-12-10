package com.wallet.payment_management.dto.request;

import com.wallet.payment_management.enums.PaymentFeeTypeEnum;
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
@Schema(description = "Ödeme işlemi ücreti oluşturma isteği")
public class PaymentTransactionFeeRequest {

    @NotNull(message = "Payment transaction ID is required")
    @Schema(description = "Ödeme işlemi ID", example = "1", required = true)
    private Long paymentTransactionId;

    @NotNull(message = "Fee type is required")
    @Schema(description = "Ücret tipi", example = "GATEWAY_FEE", required = true)
    private PaymentFeeTypeEnum feeType;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.00", message = "Amount must be greater than or equal to 0")
    @Schema(description = "Ücret tutarı", example = "2.50", required = true)
    private BigDecimal amount;

    @Schema(description = "Açıklama", example = "Gateway ücreti")
    private String description;
}
