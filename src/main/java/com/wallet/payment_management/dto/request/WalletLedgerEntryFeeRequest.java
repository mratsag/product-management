package com.wallet.payment_management.dto.request;

import com.wallet.payment_management.enums.WalletFeeTypeEnum;
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
@Schema(description = "Cüzdan hareketi ücreti oluşturma isteği")
public class WalletLedgerEntryFeeRequest {

    @NotNull(message = "Wallet ledger entry ID is required")
    @Schema(description = "Cüzdan hareketi ID", example = "1", required = true)
    private Long walletLedgerEntryId;

    @NotNull(message = "Fee type is required")
    @Schema(description = "Ücret tipi", example = "SERVICE_FEE", required = true)
    private WalletFeeTypeEnum feeType;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.00", message = "Amount must be greater than or equal to 0")
    @Schema(description = "Ücret tutarı", example = "5.00", required = true)
    private BigDecimal amount;

    @Schema(description = "Açıklama", example = "Servis ücreti")
    private String description;
}
