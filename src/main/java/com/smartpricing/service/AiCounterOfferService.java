package com.smartpricing.service;

import org.springframework.stereotype.Service;

@Service
public class AiCounterOfferService {

    /**
     * Generates a smart counter-offer price
     */
    public double generateCounterOffer(
            double basePrice,
            double offeredPrice,
            double trustScore,
            int attemptNumber
    ) {

        // ===============================
        // 1️⃣ TRUST-BASED FLEXIBILITY
        // ===============================
        double maxDiscount =
                trustScore >= 80 ? 0.20 :
                trustScore >= 60 ? 0.15 :
                trustScore >= 40 ? 0.10 :
                0.05;

        // ===============================
        // 2️⃣ ATTEMPT-BASED STRICTNESS
        // ===============================
        double attemptPenalty = attemptNumber * 0.02; // 2% stricter per attempt
        maxDiscount = Math.max(0.03, maxDiscount - attemptPenalty);

        // ===============================
        // 3️⃣ MIN ACCEPTABLE PRICE
        // ===============================
        double minAcceptablePrice = basePrice * (1 - maxDiscount);

        // ===============================
        // 4️⃣ COUNTER LOGIC
        // ===============================
        double midpoint = (offeredPrice + minAcceptablePrice) / 2;

        // Ensure bounds
        double counterPrice = Math.max(minAcceptablePrice, midpoint);
        counterPrice = Math.min(counterPrice, basePrice);

        // ===============================
        // 5️⃣ ROUNDING (CLEAN UX)
        // ===============================
        return Math.round(counterPrice);
    }
}
