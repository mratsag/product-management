package com.wallet.payment_management.dto.request;

import com.wallet.payment_management.enums.WalletLedgerEntryTypeEnum;
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
@Schema(description = "Cüzdan hareketi oluşturma isteği")
public class WalletLedgerEntryRequest {

    @NotNull(message = "Wallet account ID is required")
    @Schema(description = "Cüzdan hesabı ID", example = "1", required = true)
    private Long walletAccountId;

    @NotNull(message = "Entry type is required")
    @Schema(description = "Hareket tipi", example = "LOAD", required = true)
    private WalletLedgerEntryTypeEnum entryType;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Schema(description = "Tutar", example = "100.00", required = true)
    private BigDecimal amount;

    @NotNull(message = "Method is required")
    @Schema(description = "Ödeme yöntemi", example = "CARD", required = true)
    private String method;

    @Schema(description = "Referans numarası", example = "TXN-12345")
    private String reference;

    @Schema(description = "Açıklama", example = "Sipariş ödemesi")
    private String description;
}
