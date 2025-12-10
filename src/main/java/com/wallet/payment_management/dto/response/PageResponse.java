package com.wallet.payment_management.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Sayfalanmış response")
public class PageResponse<T> {

    @Schema(description = "İçerik listesi")
    private List<T> content;

    @Schema(description = "Toplam eleman sayısı", example = "100")
    private long totalElements;

    @Schema(description = "Toplam sayfa sayısı", example = "5")
    private int totalPages;

    @Schema(description = "Mevcut sayfa numarası (0-based)", example = "0")
    private int pageNumber;

    @Schema(description = "Sayfa boyutu", example = "20")
    private int pageSize;

    @Schema(description = "İlk sayfa mı?", example = "true")
    private boolean first;

    @Schema(description = "Son sayfa mı?", example = "false")
    private boolean last;

    @Schema(description = "Boş mu?", example = "false")
    private boolean empty;

    @Schema(description = "Toplam eleman sayısı (sayfa içinde)", example = "20")
    private int numberOfElements;
}
