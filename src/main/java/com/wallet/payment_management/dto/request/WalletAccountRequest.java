package com.wallet.payment_management.dto.request;

import com.wallet.payment_management.enums.WalletAccountTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Yeni cüzdan hesabı oluşturma isteği")
public class WalletAccountRequest {

    @NotNull(message = "Customer ID is required")
    @Schema(description = "Müşteri ID", example = "12345", required = true)
    private Long customerId;

    @Schema(description = "Hesap tipi", example = "STANDARD", defaultValue = "STANDARD")
    @Builder.Default
    private WalletAccountTypeEnum accountType = WalletAccountTypeEnum.STANDARD;

    @NotNull(message = "Currency code is required")
    @Schema(description = "Para birimi kodu", example = "TRY", required = true)
    private String currencyCode;
}
