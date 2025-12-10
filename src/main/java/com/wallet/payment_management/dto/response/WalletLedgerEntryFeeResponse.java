package com.wallet.payment_management.dto.response;

import com.wallet.payment_management.enums.WalletFeeTypeEnum;
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
@Schema(description = "Cüzdan hareketi ücreti yanıtı")
public class WalletLedgerEntryFeeResponse {

    @Schema(description = "Ücret ID", example = "1")
    private Long id;

    @Schema(description = "Cüzdan hareketi ID", example = "1")
    private Long walletLedgerEntryId;

    @Schema(description = "Ücret tipi", example = "SERVICE_FEE")
    private WalletFeeTypeEnum feeType;

    @Schema(description = "Ücret tutarı", example = "5.00")
    private BigDecimal amount;

    @Schema(description = "Açıklama", example = "Servis ücreti")
    private String description;

    @Schema(description = "Oluşturulma tarihi", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;
}
