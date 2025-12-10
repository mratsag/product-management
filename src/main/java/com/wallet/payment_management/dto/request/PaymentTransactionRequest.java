package com.wallet.payment_management.dto.request;

import com.wallet.payment_management.enums.PaymentTransactionTypeEnum;
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
@Schema(description = "Ödeme işlemi oluşturma isteği")
public class PaymentTransactionRequest {

    @NotNull(message = "Payment ID is required")
    @Schema(description = "Ödeme ID", example = "1", required = true)
    private Long paymentId;

    @NotNull(message = "Transaction type is required")
    @Schema(description = "İşlem tipi", example = "AUTH", required = true)
    private PaymentTransactionTypeEnum transactionType;

    @NotNull(message = "Paid amount is required")
    @DecimalMin(value = "0.01", message = "Paid amount must be greater than 0")
    @Schema(description = "İşlem tutarı", example = "250.00", required = true)
    private BigDecimal paidAmount;

    @NotNull(message = "Method is required")
    @Schema(description = "Ödeme yöntemi", example = "CARD", required = true)
    private String method;

    @Schema(description = "Referans numarası", example = "TXN-12345")
    private String reference;
}
