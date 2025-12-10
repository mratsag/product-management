package com.wallet.payment_management.dto.response;

import com.wallet.payment_management.enums.WalletAccountStatusEnum;
import com.wallet.payment_management.enums.WalletAccountTypeEnum;
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
@Schema(description = "Cüzdan hesabı yanıtı")
public class WalletAccountResponse {

    @Schema(description = "Cüzdan hesabı ID", example = "1")
    private Long id;

    @Schema(description = "Müşteri ID", example = "12345")
    private Long customerId;

    @Schema(description = "Hesap tipi", example = "STANDARD")
    private WalletAccountTypeEnum accountType;

    @Schema(description = "Para birimi kodu", example = "TRY")
    private String currencyCode;

    @Schema(description = "Mevcut bakiye", example = "1000.00")
    private BigDecimal currentBalance;

    @Schema(description = "Durum", example = "ACTIVE")
    private WalletAccountStatusEnum status;

    @Schema(description = "Oluşturulma tarihi", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Güncellenme tarihi", example = "2024-01-15T10:30:00")
    private LocalDateTime updatedAt;

    @Schema(description = "Kapanış tarihi", example = "2024-01-20T15:00:00")
    private LocalDateTime closedAt;

    @Schema(description = "Cüzdan hareketleri listesi")
    private List<WalletLedgerEntryResponse> ledgerEntries;
}
