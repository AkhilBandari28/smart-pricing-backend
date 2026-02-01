package com.smartpricing.service;

import com.smartpricing.entity.User;
import com.smartpricing.exception.BusinessException;
import com.smartpricing.repository.NegotiationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OfferAbuseDetectionService {

    private final NegotiationRepository negotiationRepository;

    public OfferAbuseDetectionService(NegotiationRepository negotiationRepository) {
        this.negotiationRepository = negotiationRepository;
    }

    /**
     * 🚨 Detect abusive negotiation behavior BEFORE AI / Rule Engine
     */
    public void validateOffer(
            User user,
            Long productId,
            double basePrice,
            double offeredPrice
    ) {

        // -------------------------------
        // 1️⃣ Extreme Lowball Protection
        // -------------------------------
        double minAllowedPrice = basePrice * 0.4; // 60% drop max
        if (offeredPrice < minAllowedPrice) {
            throw new BusinessException(
                    "Offer rejected: Price too low to be considered."
            );
        }

        // ---------------------------------------
        // 2️⃣ Rapid Retry (Bot / Script Detection)
        // ---------------------------------------
        LocalDateTime last5Minutes = LocalDateTime.now().minusMinutes(5);

        long recentAttempts =
                negotiationRepository.countRecentAttempts(
                        user.getUserId(),
                        productId,
                        last5Minutes
                );

        if (recentAttempts >= 3) {
            throw new BusinessException(
                    "Too many negotiation attempts in a short time. Please wait before retrying."
            );
        }
    }
}
