package com.smartpricing.util;

public class PriceLockUtil {

    private PriceLockUtil() {
        // Utility class - prevent object creation
    }

    public static boolean isDealAllowed(Double offeredPrice, Double minPrice) {
        return offeredPrice != null && minPrice != null && offeredPrice >= minPrice;
    }

    public static Double lockFinalPrice(Double agreedPrice) {
        return Math.round(agreedPrice * 100.0) / 100.0;
    }
}
