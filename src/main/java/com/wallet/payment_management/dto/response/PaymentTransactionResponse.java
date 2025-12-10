package com.wallet.payment_management.dto.response;

import com.wallet.payment_management.enums.PaymentTransactionStatusEnum;
import com.wallet.payment_management.enums.PaymentTransactionTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ödeme işlemi yanıtı")
public class PaymentTransactionResponse {

    @Schema(description = "İşlem ID", example = "1")
    private Long id;

    @Schema(description = "Ödeme ID", example = "1")
    private Long paymentId;

    @Schema(description = "İşlem tipi", example = "CAPTURE")
    private PaymentTransactionTypeEnum transactionType;

    @Schema(description = "İşlem tutarı", example = "250.00")
    private BigDecimal paidAmount;

    @Schema(description = "Ödeme yöntemi", example = "CARD")
    private String method;

    @Schema(description = "Durum", example = "SUCCESS")
    private PaymentTransactionStatusEnum status;

    @Schema(description = "Referans numarası", example = "TXN-12345")
    private String reference;

    @Schema(description = "Oluşturulma tarihi", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Ücretler listesi")
    private List<PaymentTransactionFeeResponse> fees;
}
