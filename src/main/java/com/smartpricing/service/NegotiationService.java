package com.smartpricing.service;

import com.smartpricing.ai.AiNegotiationClient;
import com.smartpricing.ai.dto.AiNegotiationRequest;
import com.smartpricing.ai.dto.AiNegotiationResponse;
import com.smartpricing.dto.NegotiationRequestDto;
import com.smartpricing.dto.NegotiationResponseDto;
import com.smartpricing.entity.*;
import com.smartpricing.exception.BusinessException;
import com.smartpricing.repository.NegotiationRepository;
import com.smartpricing.repository.ProductRepository;
import com.smartpricing.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NegotiationService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final NegotiationRepository negotiationRepository;
    private final AiNegotiationClient aiNegotiationClient;

    private final TrustScoreService trustScoreService;
    private final OfferAbuseDetectionService offerAbuseDetectionService;
    private final AiSafetyGuardrailService aiSafetyGuardrailService;
    private final AiCooldownService aiCooldownService;

    public NegotiationService(
            ProductRepository productRepository,
            UserRepository userRepository,
            NegotiationRepository negotiationRepository,
            AiNegotiationClient aiNegotiationClient,
            TrustScoreService trustScoreService,
            OfferAbuseDetectionService offerAbuseDetectionService,
            AiSafetyGuardrailService aiSafetyGuardrailService,
            AiCooldownService aiCooldownService
    ) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.negotiationRepository = negotiationRepository;
        this.aiNegotiationClient = aiNegotiationClient;
        this.trustScoreService = trustScoreService;
        this.offerAbuseDetectionService = offerAbuseDetectionService;
        this.aiSafetyGuardrailService = aiSafetyGuardrailService;
        this.aiCooldownService = aiCooldownService;
    }

    // =====================================================
    // MAIN ENTRY POINT
    // =====================================================
    public NegotiationResponseDto negotiate(
            NegotiationRequestDto request,
            String userEmail,
            String decisionMode
    ) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BusinessException("User not found"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new BusinessException("Product not found"));

        // 🔒 STEP-16 — COUNTER LOCK (FIXED)
        negotiationRepository
                .findTopByUserIdAndProductIdOrderByCreatedAtDesc(
                        user.getUserId(),
                        product.getProductId()
                )
                .ifPresent(last -> {
                    if (last.getStatus() == NegotiationStatus.COUNTER &&
                        last.getLockExpiresAt() != null &&
                        last.getLockExpiresAt().isAfter(LocalDateTime.now())) {

                        throw new BusinessException(
                                "Please wait before responding to the counter offer."
                        );
                    }
                });

        long attempts = negotiationRepository
                .countByUserIdAndProductId(user.getUserId(), product.getProductId());

        if (attempts >= 3) {
            throw new BusinessException("Negotiation limit reached (Max 3 attempts)");
        }

        // 🔥 STEP-12A — OFFER ABUSE DETECTION
        offerAbuseDetectionService.validateOffer(
                user,
                product.getProductId(),
                product.getBasePrice(),
                request.getOfferedPrice()
        );

        // ⏱ STEP-14 — AI COOLDOWN
        if ("AI".equalsIgnoreCase(decisionMode)) {
            aiCooldownService.validateCooldown(
                    user.getUserId(),
                    product.getProductId()
            );
        }

        double basePrice = product.getBasePrice();
        double offeredPrice = request.getOfferedPrice();
//        double trustScore = user.getTrustScore();
        double trustScore = user.getTrustScore() != null
                ? user.getTrustScore()
                : 50.0; // default neutral trust

        int attemptNumber = (int) attempts + 1;

        if ("AI".equalsIgnoreCase(decisionMode)) {
            return aiDecision(user, product, offeredPrice, basePrice, trustScore, attemptNumber);
        }

        return ruleBasedDecision(user, product, offeredPrice, basePrice, trustScore, attemptNumber);
    }

    // =====================================================
    // RULE-BASED ENGINE
    // =====================================================
    private NegotiationResponseDto ruleBasedDecision(
            User user,
            Product product,
            double offeredPrice,
            double basePrice,
            double trustScore,
            int attemptNumber
    ) {

        double maxDiscount =
                trustScore >= 80 ? 0.20 :
                trustScore >= 50 ? 0.10 :
                0.05;

        double minAcceptablePrice = basePrice * (1 - maxDiscount);

        Negotiation negotiation = new Negotiation();
        negotiation.setUserId(user.getUserId());
        negotiation.setProductId(product.getProductId());
        negotiation.setBasePrice(basePrice);
        negotiation.setTrustScore(trustScore);
        negotiation.setMaxDiscount(maxDiscount);
        negotiation.setOfferedPrice(offeredPrice);
        negotiation.setAttemptNumber(attemptNumber);
        negotiation.setCreatedAt(LocalDateTime.now());

        NegotiationResponseDto response = new NegotiationResponseDto();

        if (offeredPrice >= minAcceptablePrice) {

            negotiation.setStatus(NegotiationStatus.ACCEPTED);
            negotiation.setFinalPrice(Math.max(offeredPrice, minAcceptablePrice));
            negotiation.setDecisionReason("RULE_ENGINE_ACCEPT");
            negotiation.setLockExpiresAt(LocalDateTime.now().plusMinutes(10));

            negotiationRepository.save(negotiation);

            trustScoreService.updateTrustScore(
                    user.getUserId(), +2, "RULE_ACCEPT",
                    negotiation.getNegotiationId()
            );

            response.setStatus("ACCEPTED");
            response.setFinalPrice(negotiation.getFinalPrice());
            response.setMessage("Offer accepted based on trust score (" + trustScore + ").");

        } else {

            negotiation.setStatus(NegotiationStatus.REJECTED);
            negotiation.setDecisionReason("RULE_ENGINE_REJECT");
            negotiationRepository.save(negotiation);

            trustScoreService.updateTrustScore(
                    user.getUserId(), -1, "RULE_REJECT",
                    negotiation.getNegotiationId()
            );

            response.setStatus("REJECTED");
            response.setMessage("Offer rejected.");
        }

        response.setAttemptNumber(attemptNumber);
        return response;
    }

    // =====================================================
    // AI ENGINE (SAFE + COUNTER SUPPORT)
    // =====================================================
    private NegotiationResponseDto aiDecision(
            User user,
            Product product,
            double offeredPrice,
            double basePrice,
            double trustScore,
            int attemptNumber
    ) {

        Negotiation negotiation = new Negotiation();
        negotiation.setUserId(user.getUserId());
        negotiation.setProductId(product.getProductId());
        negotiation.setBasePrice(basePrice);
        negotiation.setTrustScore(trustScore);
        negotiation.setOfferedPrice(offeredPrice);
        negotiation.setAttemptNumber(attemptNumber);
        negotiation.setCreatedAt(LocalDateTime.now());

        NegotiationResponseDto response = new NegotiationResponseDto();

        try {
            AiNegotiationRequest aiRequest = new AiNegotiationRequest();
            aiRequest.setUserId(user.getUserId());
            aiRequest.setProductId(product.getProductId());
            aiRequest.setBasePrice(basePrice);
            aiRequest.setOfferedPrice(offeredPrice);
            aiRequest.setTrustScore(trustScore);
            aiRequest.setAttemptNumber(attemptNumber);

            AiNegotiationResponse aiResponse =
                    aiNegotiationClient.negotiate(aiRequest);

            aiResponse = aiSafetyGuardrailService.applyGuardrails(
                    aiResponse, basePrice, offeredPrice, trustScore, attemptNumber
            );

            // ✅ FINAL PRICE FALLBACK FIX
            if ("ACCEPT".equalsIgnoreCase(aiResponse.getDecision())
                    && aiResponse.getFinalPrice() == null) {
                aiResponse.setFinalPrice(offeredPrice);
            }

            negotiation.setAiConfidence(aiResponse.getConfidenceScore());
            negotiation.setDecisionReason(aiResponse.getExplanation());

            switch (aiResponse.getDecision().toUpperCase()) {

                case "ACCEPT" -> {
                    negotiation.setStatus(NegotiationStatus.ACCEPTED);
                    negotiation.setFinalPrice(aiResponse.getFinalPrice());
                    negotiation.setLockExpiresAt(LocalDateTime.now().plusMinutes(10));
                    negotiationRepository.save(negotiation);

                    trustScoreService.updateTrustScore(
                            user.getUserId(), +3, "AI_ACCEPT",
                            negotiation.getNegotiationId()
                    );

                    response.setStatus("ACCEPTED");
                    response.setFinalPrice(aiResponse.getFinalPrice());
                    response.setMessage(aiResponse.getExplanation());
                }

                case "COUNTER" -> {
                    negotiation.setStatus(NegotiationStatus.COUNTER);
                    negotiation.setFinalPrice(aiResponse.getFinalPrice());
                    negotiation.setLockExpiresAt(LocalDateTime.now().plusMinutes(3));
                    negotiationRepository.save(negotiation);

                    response.setStatus("COUNTER");
                    response.setFinalPrice(aiResponse.getFinalPrice());
                    response.setMessage(aiResponse.getExplanation());
                }

                default -> {
                    negotiation.setStatus(NegotiationStatus.REJECTED);
                    negotiationRepository.save(negotiation);

                    trustScoreService.updateTrustScore(
                            user.getUserId(), -1, "AI_REJECT",
                            negotiation.getNegotiationId()
                    );

                    response.setStatus("REJECTED");
                    response.setMessage(aiResponse.getExplanation());
                }
            }

            response.setAttemptNumber(attemptNumber);

        } catch (Exception ex) {
            return ruleBasedDecision(
                    user, product, offeredPrice, basePrice, trustScore, attemptNumber
            );
        }

        return response;
    }
}
