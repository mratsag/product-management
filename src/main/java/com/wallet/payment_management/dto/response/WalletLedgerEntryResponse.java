package com.wallet.payment_management.dto.response;

import com.wallet.payment_management.enums.WalletLedgerEntryDirectionEnum;
import com.wallet.payment_management.enums.WalletLedgerEntryStatusEnum;
import com.wallet.payment_management.enums.WalletLedgerEntryTypeEnum;
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
@Schema(description = "Cüzdan hareketi yanıtı")
public class WalletLedgerEntryResponse {

    @Schema(description = "Hareket ID", example = "1")
    private Long id;

    @Schema(description = "Cüzdan hesabı ID", example = "1")
    private Long walletAccountId;

    @Schema(description = "Hareket tipi", example = "LOAD")
    private WalletLedgerEntryTypeEnum entryType;

    @Schema(description = "Hareket yönü", example = "CREDIT")
    private WalletLedgerEntryDirectionEnum entryDirection;

    @Schema(description = "Tutar", example = "100.00")
    private BigDecimal amount;

    @Schema(description = "İşlem sonrası bakiye", example = "1100.00")
    private BigDecimal balanceAfter;

    @Schema(description = "Durum", example = "POSTED")
    private WalletLedgerEntryStatusEnum status;

    @Schema(description = "Ödeme yöntemi", example = "CARD")
    private String method;

    @Schema(description = "Referans numarası", example = "TXN-12345")
    private String reference;

    @Schema(description = "Açıklama", example = "Sipariş ödemesi")
    private String description;

    @Schema(description = "Oluşturulma tarihi", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Ücretler listesi")
    private List<WalletLedgerEntryFeeResponse> fees;
}
