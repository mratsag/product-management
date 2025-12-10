package com.wallet.payment_management.dto.response;

import com.wallet.payment_management.enums.PaymentStatusEnum;
import com.wallet.payment_management.enums.PaymentTypeEnum;
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
@Schema(description = "Ödeme yanıtı")
public class PaymentResponse {

    @Schema(description = "Ödeme ID", example = "1")
    private Long id;

    @Schema(description = "Ödeme tipi", example = "ORDER")
    private PaymentTypeEnum paymentType;

    @Schema(description = "Toplam tutar", example = "250.00")
    private BigDecimal amount;

    @Schema(description = "Ödenen tutar", example = "250.00")
    private BigDecimal paidAmount;

    @Schema(description = "Durum", example = "PAID")
    private PaymentStatusEnum status;

    @Schema(description = "Açıklama", example = "Sipariş ödemesi")
    private String description;

    @Schema(description = "Oluşturulma tarihi", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Güncellenme tarihi", example = "2024-01-15T10:30:00")
    private LocalDateTime updatedAt;

    @Schema(description = "Ödeme işlemleri listesi")
    private List<PaymentTransactionResponse> transactions;

    @Schema(description = "Sipariş tahsisleri listesi")
    private List<OrderPaymentAllocationResponse> allocations;
}
