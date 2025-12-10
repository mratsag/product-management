package com.wallet.payment_management.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "API hata yanıtı")
public class ErrorResponse {

    @Schema(description = "Hatanın oluştuğu zaman", example = "2024-01-15T10:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "HTTP durum kodu", example = "400")
    private int status;

    @Schema(description = "Hata tipi", example = "Bad Request")
    private String error;

    @Schema(description = "Hata mesajı", example = "Geçersiz istek verisi")
    private String message;

    @Schema(description = "İstek path'i", example = "/api/wallet-accounts")
    private String path;

    @Schema(description = "Detaylı hata mesajları (validation hataları için)", example = "[\"customerId: Customer ID is required\"]")
    private List<String> details;
}
