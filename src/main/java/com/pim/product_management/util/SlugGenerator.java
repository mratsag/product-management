package com.pim.product_management.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public class SlugGenerator {

    private static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    public static String generateSlug(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Input cannot be null or empty");
        }

        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String slug = normalized.toLowerCase(Locale.ENGLISH);

        // Türkçe karakterleri dönüştür
        slug = slug.replace("ı", "i")
                .replace("ğ", "g")
                .replace("ü", "u")
                .replace("ş", "s")
                .replace("ö", "o")
                .replace("ç", "c");

        // Boşlukları tire ile değiştir
        slug = WHITESPACE.matcher(slug).replaceAll("-");

        // Latin olmayan karakterleri kaldır
        slug = NON_LATIN.matcher(slug).replaceAll("");

        // Birden fazla tire varsa tek tireye düşür
        slug = slug.replaceAll("-+", "-");

        // Başta ve sondaki tireleri kaldır
        slug = slug.replaceAll("^-|-$", "");

        return slug;
    }
}