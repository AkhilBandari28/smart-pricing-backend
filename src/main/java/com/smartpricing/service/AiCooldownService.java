package com.smartpricing.service;

import com.smartpricing.entity.Negotiation;
import com.smartpricing.entity.NegotiationStatus;
import com.smartpricing.exception.BusinessException;
import com.smartpricing.repository.NegotiationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AiCooldownService {

    private final NegotiationRepository negotiationRepository;

    public AiCooldownService(NegotiationRepository negotiationRepository) {
        this.negotiationRepository = negotiationRepository;
    }

    /**
     * AI Cooldown Rules (USER FRIENDLY)
     *
     * COUNTER  → immediate retry allowed
     * ACCEPTED → lock until lockExpiresAt
     * REJECTED → block ONLY if spam (3+ attempts in 60s)
     */
    public void validateCooldown(Long userId, Long productId) {

        Optional<Negotiation> lastOpt =
                negotiationRepository.findTopByUserIdAndProductIdOrderByCreatedAtDesc(
                        userId, productId
                );

        if (lastOpt.isEmpty()) {
            return;
        }

        Negotiation last = lastOpt.get();
        LocalDateTime now = LocalDateTime.now();

        // ✅ COUNTER → must respond immediately
        if (last.getStatus() == NegotiationStatus.COUNTER) {
            return;
        }

        // 🔒 ACCEPTED → enforce price lock
        if (last.getStatus() == NegotiationStatus.ACCEPTED) {
            if (last.getLockExpiresAt() != null &&
                last.getLockExpiresAt().isAfter(now)) {

                throw new BusinessException(
                        "Offer already accepted. Please wait before negotiating again."
                );
            }
        }

        // 🔁 REJECTED → spam protection only
        if (last.getStatus() == NegotiationStatus.REJECTED) {

            long recentAttempts =
                    negotiationRepository.countRecentAttempts(
                            userId,
                            productId,
                            now.minusSeconds(60)
                    );

            if (recentAttempts >= 3) {
                throw new BusinessException(
                        "Too many rapid attempts. Please wait 1 minute before retrying."
                );
            }
        }
    }
}
