package com.smartpricing.intelligence;

import com.smartpricing.entity.Product;
import com.smartpricing.entity.User;
import org.springframework.stereotype.Component;

@Component
public class NegotiationDecisionEngine {

    public NegotiationResult decide(
            User user,
            Product product,
            double offeredPrice,
            int attempt
    ) {

        double basePrice = product.getBasePrice();
        double trustScore = user.getTrustScore();

        double maxDiscount;
        if (trustScore >= 80) maxDiscount = 0.20;
        else if (trustScore >= 50) maxDiscount = 0.10;
        else maxDiscount = 0.05;

        double minAcceptablePrice = basePrice * (1 - maxDiscount);

        // 🧠 Decision logic
        if (offeredPrice >= basePrice) {
            return NegotiationResult.accept(
                    basePrice,
                    "Offer accepted at base price"
            );
        }

        if (offeredPrice >= minAcceptablePrice) {
            return NegotiationResult.accept(
                    offeredPrice,
                    "Offer accepted"
            );
        }

        if (offeredPrice >= minAcceptablePrice * 0.9 && attempt < 3) {
            return NegotiationResult.counter(
                    minAcceptablePrice,
                    "Counter offer generated"
            );
        }

        return NegotiationResult.reject("Offer too low");
    }
}
