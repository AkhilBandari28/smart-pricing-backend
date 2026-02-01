//package com.smartpricing.service;
//
//import com.smartpricing.ai.dto.AiNegotiationResponse;
//import org.springframework.stereotype.Service;
//
///**
// * STEP-12B — AI SAFETY GUARDRAILS
// *
// * Responsibilities:
// * 1. Enforce hard business limits
// * 2. Prevent unrealistic discounts
// * 3. Normalize AI confidence
// * 4. Provide safe fallback decisions
// * 5. Guarantee explainability
// */
//@Service
//public class AiSafetyGuardrailService {
//
//    public AiNegotiationResponse applyGuardrails(
//            AiNegotiationResponse aiResponse,
//            double basePrice,
//            double offeredPrice,
//            double trustScore,
//            int attemptNumber
//    ) {
//
//        // =====================================================
//        // 🚨 GLOBAL HARD FLOOR (ABSOLUTE SAFETY)
//        // Never allow more than 30% discount (for ANY user)
//        // =====================================================
//        double absoluteMinPrice = basePrice * 0.70;
//        if (offeredPrice < absoluteMinPrice) {
//            return reject(
//                    "Offer too low. Maximum allowed discount is 30%."
//            );
//        }
//
//        // =====================================================
//        // 1️⃣ NULL / BROKEN AI RESPONSE → SAFE FALLBACK
//        // =====================================================
//        if (aiResponse == null || aiResponse.getDecision() == null) {
//
//            double minAllowedPrice = calculateMinAllowedPrice(basePrice, trustScore);
//
//            if (offeredPrice >= minAllowedPrice) {
//                AiNegotiationResponse fallback = new AiNegotiationResponse();
//                fallback.setDecision("ACCEPT");
//                fallback.setFinalPrice(offeredPrice);
//                fallback.setConfidenceScore(0.70);
//                fallback.setExplanation(
//                        "Accepted using rule-based fallback logic."
//                );
//                return fallback;
//            }
//
//            return reject("Offer rejected by fallback safety rules.");
//        }
//
//        String decision = aiResponse.getDecision().toUpperCase();
//
//        // =====================================================
//        // 2️⃣ DECISION VALIDATION
//        // =====================================================
//        if (!decision.equals("ACCEPT")
//                && !decision.equals("REJECT")
//                && !decision.equals("COUNTER")) {
//
//            return reject(
//                    "Invalid AI decision. Offer rejected for safety."
//            );
//        }
//
//        // =====================================================
//        // 3️⃣ ACCEPT SAFETY CHECKS
//        // =====================================================
//        if (decision.equals("ACCEPT")) {
//
//            if (aiResponse.getFinalPrice() == null) {
//                return reject(
//                        "AI accepted offer without final price."
//                );
//            }
//
//            double finalPrice = aiResponse.getFinalPrice();
//
//            // Cap at base price
//            if (finalPrice > basePrice) {
//                finalPrice = basePrice;
//            }
//
//            double minAllowedPrice =
//                    calculateMinAllowedPrice(basePrice, trustScore);
//
//            // Trust-based + absolute floor enforcement
//            if (finalPrice < minAllowedPrice || finalPrice < absoluteMinPrice) {
//                return reject(
//                        "Offer rejected due to excessive discount."
//                );
//            }
//
//            aiResponse.setFinalPrice(finalPrice);
//        }
//
//        // =====================================================
//        // 4️⃣ CONFIDENCE NORMALIZATION
//        // =====================================================
//        double confidence = aiResponse.getConfidenceScore();
//
//        if (confidence < 0) confidence = 0.0;
//        if (confidence > 1) confidence = 1.0;
//
//        // =====================================================
//        // 5️⃣ DECISION-AWARE CONFIDENCE ADJUSTMENT
//        // =====================================================
//        switch (decision) {
//            case "ACCEPT" -> confidence = Math.max(confidence, 0.70);
//            case "COUNTER" -> confidence = clamp(confidence, 0.45, 0.70);
//            case "REJECT" -> confidence = clamp(confidence, 0.25, 0.60);
//        }
//
//        aiResponse.setConfidenceScore(confidence);
//
//        // =====================================================
//        // 6️⃣ LOW CONFIDENCE BLOCK (ACCEPT ONLY)
//        // =====================================================
//        if (decision.equals("ACCEPT") && confidence < 0.35) {
//            return reject(
//                    "AI confidence too low to safely accept the offer."
//            );
//        }
//
//        // =====================================================
//        // 7️⃣ ATTEMPT LIMIT CONTROL
//        // =====================================================
//        if (decision.equals("COUNTER") && attemptNumber >= 3) {
//            return reject(
//                    "Maximum negotiation attempts reached."
//            );
//        }
//
//        // =====================================================
//        // 8️⃣ EXPLANATION SAFETY
//        // =====================================================
//        if (aiResponse.getExplanation() == null
//                || aiResponse.getExplanation().isBlank()) {
//
//            aiResponse.setExplanation(
//                    "Decision based on pricing rules, trust score, and negotiation history."
//            );
//        }
//
//        return aiResponse;
//    }
//
//    // =====================================================
//    // 🔧 TRUST-BASED MIN PRICE CALCULATOR
//    // =====================================================
//    private double calculateMinAllowedPrice(double basePrice, double trustScore) {
//        if (trustScore >= 80) return basePrice * 0.80; // max 20% discount
//        if (trustScore >= 50) return basePrice * 0.90; // max 10% discount
//        return basePrice * 0.95;                       // max 5% discount
//    }
//
//    // =====================================================
//    // 🔒 SAFE REJECTION HELPER
//    // =====================================================
//    private AiNegotiationResponse reject(String message) {
//        AiNegotiationResponse response = new AiNegotiationResponse();
//        response.setDecision("REJECT");
//        response.setFinalPrice(null);
//        response.setConfidenceScore(0.30);
//        response.setExplanation(message);
//        return response;
//    }
//
//    // =====================================================
//    // 🔢 CONFIDENCE CLAMP
//    // =====================================================
//    private double clamp(double value, double min, double max) {
//        return Math.max(min, Math.min(max, value));
//    }
//}



// !=======================================



package com.smartpricing.service;

import com.smartpricing.ai.dto.AiNegotiationResponse;
import org.springframework.stereotype.Service;

/**
 * STEP-12B — AI SAFETY GUARDRAILS
 *
 * Responsibilities:
 * 1. Enforce hard business limits
 * 2. Prevent unrealistic discounts
 * 3. Normalize & contextualize AI confidence
 * 4. Provide safe fallback decisions
 * 5. Guarantee explainability
 */
@Service
public class AiSafetyGuardrailService {

    public AiNegotiationResponse applyGuardrails(
            AiNegotiationResponse aiResponse,
            double basePrice,
            double offeredPrice,
            double trustScore,
            int attemptNumber
    ) {

        // =====================================================
        // 🚨 GLOBAL HARD FLOOR (MAX 30% DISCOUNT)
        // =====================================================
        double absoluteMinPrice = basePrice * 0.70;
        if (offeredPrice < absoluteMinPrice) {
            return reject(
                    "Offer too low. Maximum allowed discount is 30%.",
                    offeredPrice,
                    basePrice
            );
        }

        // =====================================================
        // 1️⃣ NULL / BROKEN AI RESPONSE → SAFE FALLBACK
        // =====================================================
        if (aiResponse == null || aiResponse.getDecision() == null) {

            double minAllowed = calculateMinAllowedPrice(basePrice, trustScore);

            if (offeredPrice >= minAllowed) {
                AiNegotiationResponse fallback = new AiNegotiationResponse();
                fallback.setDecision("ACCEPT");
                fallback.setFinalPrice(offeredPrice);
                fallback.setConfidenceScore(0.75); // confident fallback accept
                fallback.setExplanation(
                        "Accepted using rule-based fallback logic."
                );
                return fallback;
            }

            return reject(
                    "Offer rejected by fallback safety rules.",
                    offeredPrice,
                    basePrice
            );
        }

        String decision = aiResponse.getDecision().toUpperCase();

        // =====================================================
        // 2️⃣ DECISION VALIDATION
        // =====================================================
        if (!decision.equals("ACCEPT")
                && !decision.equals("REJECT")
                && !decision.equals("COUNTER")) {

            return reject(
                    "Invalid AI decision. Offer rejected for safety.",
                    offeredPrice,
                    basePrice
            );
        }

        // =====================================================
        // 3️⃣ ACCEPT SAFETY CHECKS
        // =====================================================
        if (decision.equals("ACCEPT")) {

            if (aiResponse.getFinalPrice() == null) {
                return reject(
                        "AI accepted offer without final price.",
                        offeredPrice,
                        basePrice
                );
            }

            double finalPrice = aiResponse.getFinalPrice();

            // Cap price at base price
            if (finalPrice > basePrice) {
                finalPrice = basePrice;
            }

            double minAllowed =
                    calculateMinAllowedPrice(basePrice, trustScore);

            if (finalPrice < minAllowed || finalPrice < absoluteMinPrice) {
                return reject(
                        "Offer rejected due to excessive discount.",
                        offeredPrice,
                        basePrice
                );
            }

            aiResponse.setFinalPrice(finalPrice);
        }

        // =====================================================
        // 4️⃣ CONFIDENCE NORMALIZATION
        // =====================================================
        double confidence = aiResponse.getConfidenceScore();
        if (confidence < 0) confidence = 0.0;
        if (confidence > 1) confidence = 1.0;

        // =====================================================
        // 5️⃣ DECISION-AWARE CONFIDENCE (KEY FIX 🔥)
        // =====================================================
        switch (decision) {
            case "ACCEPT" -> confidence = clamp(confidence, 0.70, 0.95);
            case "COUNTER" -> confidence = clamp(confidence, 0.45, 0.70);
            case "REJECT" -> confidence = clamp(confidence, 0.80, 0.98);
        }

        aiResponse.setConfidenceScore(confidence);

        // =====================================================
        // 6️⃣ LOW CONFIDENCE BLOCK (ACCEPT ONLY)
        // =====================================================
        if (decision.equals("ACCEPT") && confidence < 0.35) {
            return reject(
                    "AI confidence too low to safely accept the offer.",
                    offeredPrice,
                    basePrice
            );
        }

        // =====================================================
        // 7️⃣ ATTEMPT LIMIT CONTROL
        // =====================================================
        if (decision.equals("COUNTER") && attemptNumber >= 3) {
            return reject(
                    "Maximum negotiation attempts reached.",
                    offeredPrice,
                    basePrice
            );
        }

        // =====================================================
        // 8️⃣ EXPLANATION SAFETY
        // =====================================================
        if (aiResponse.getExplanation() == null
                || aiResponse.getExplanation().isBlank()) {

            aiResponse.setExplanation(
                    "Decision based on pricing rules, trust score, and negotiation history."
            );
        }

        return aiResponse;
    }

    // =====================================================
    // 🔧 TRUST-BASED MIN PRICE
    // =====================================================
    private double calculateMinAllowedPrice(double basePrice, double trustScore) {
        if (trustScore >= 80) return basePrice * 0.80; // 20%
        if (trustScore >= 50) return basePrice * 0.90; // 10%
        return basePrice * 0.95;                       // 5%
    }

    // =====================================================
    // 🔒 SAFE REJECTION
    // =====================================================
    private AiNegotiationResponse reject(
            String message,
            double offeredPrice,
            double basePrice
    ) {
        AiNegotiationResponse response = new AiNegotiationResponse();
        response.setDecision("REJECT");
        response.setFinalPrice(null);
        response.setConfidenceScore(0.90); // confident rejection
        response.setExplanation(message);
        return response;
    }

    // =====================================================
    // 🔢 CONFIDENCE CLAMP
    // =====================================================
    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}

