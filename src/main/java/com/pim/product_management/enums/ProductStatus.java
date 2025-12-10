package com.pim.product_management.enums;

public enum ProductStatus {
    DRAFT("Taslak"),
    ACTIVE("Aktif"),
    ARCHIVED("Arşivlenmiş");

    private final String displayName;

    ProductStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}