package com.wallet.payment_management.dto.request;

import com.wallet.payment_management.enums.PaymentTypeEnum;
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
@Schema(description = "Ödeme oluşturma isteği")
public class PaymentRequest {

    @NotNull(message = "Payment type is required")
    @Schema(description = "Ödeme tipi", example = "ORDER", required = true)
    private PaymentTypeEnum paymentType;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Schema(description = "Toplam tutar", example = "250.00", required = true)
    private BigDecimal amount;

    @Schema(description = "Açıklama", example = "Sipariş ödemesi")
    private String description;
}
