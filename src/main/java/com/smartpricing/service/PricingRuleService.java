package com.smartpricing.service;

import org.springframework.stereotype.Service;

@Service
public class PricingRuleService {

    public boolean isPriceAllowed(Double offeredPrice, Double minPrice) {
        return offeredPrice >= minPrice;
    }

    public Double applyDiscount(Double basePrice, Double discountPercentage) {
        return basePrice - (basePrice * discountPercentage / 100);
    }
}
