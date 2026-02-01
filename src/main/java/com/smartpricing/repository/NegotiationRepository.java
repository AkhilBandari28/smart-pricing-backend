package com.smartpricing.repository;

import com.smartpricing.entity.Negotiation;
import com.smartpricing.entity.NegotiationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NegotiationRepository extends JpaRepository<Negotiation, Long> {

    long countByUserIdAndProductId(Long userId, Long productId);

    Optional<Negotiation> findFirstByUserIdAndProductIdAndStatusAndLockExpiresAtAfter(
            Long userId,
            Long productId,
            NegotiationStatus status,
            LocalDateTime now
    );

    Optional<Negotiation> findFirstByUserIdAndProductIdAndStatus(
            Long userId,
            Long productId,
            NegotiationStatus status
    );

    List<Negotiation> findByUserId(Long userId);
    List<Negotiation> findByProductId(Long productId);

    @Query("""
        SELECT COUNT(n)
        FROM Negotiation n
        WHERE n.userId = :userId
          AND n.productId = :productId
          AND n.createdAt >= :since
    """)
    long countRecentAttempts(
            @Param("userId") Long userId,
            @Param("productId") Long productId,
            @Param("since") LocalDateTime since
    );

    long countByStatus(NegotiationStatus status);

    @Query("SELECT AVG(n.aiConfidence) FROM Negotiation n")
    Double findAverageAiConfidence();

    Optional<Negotiation>
    findTopByUserIdAndProductIdOrderByCreatedAtDesc(Long userId, Long productId);

    long countByTrustScoreLessThan(Double trustScore);
    long countByTrustScoreGreaterThan(Double trustScore);

    long countByTrustScoreLessThanAndStatus(Double trustScore, NegotiationStatus status);
    long countByTrustScoreGreaterThanAndStatus(Double trustScore, NegotiationStatus status);

    @Query("SELECT AVG(n.offeredPrice / n.basePrice) FROM Negotiation n")
    Double findAveragePriceRatio();

    @Query("SELECT AVG(n.trustScore) FROM Negotiation n")
    Double findAverageTrustScore();
}
