//package com.smartpricing.entity;
//
//import jakarta.persistence.*;
//import lombok.Data;
//
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "negotiations")
//@Data
//public class Negotiation {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long negotiationId;
//
//    private Long userId;
//    private Long productId;
//
//    private Double offeredPrice;
//    private Double finalPrice;
//
//    @Enumerated(EnumType.STRING)
//    private NegotiationStatus status;
//
//    private Integer attemptNumber;
//
//    private LocalDateTime createdAt;
//
//    // 🔐 PRICE LOCK (IMPORTANT)
//    private LocalDateTime lockExpiresAt;
//}


// AI FEATURES--------------------------------



package com.smartpricing.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "negotiations")
@Data
public class Negotiation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long negotiationId;

    // =========================
    // 🔗 BASIC RELATION IDS
    // =========================
    private Long userId;
    private Long productId;

    // =========================
    // 💰 PRICING DATA
    // =========================
    private Double offeredPrice;
    private Double finalPrice;

    // =========================
    // 🤖 AI TRAINING FEATURES (NEW)
    // =========================

    /**
     * Base product price at negotiation time
     * Used by ML model to learn discount patterns
     */
    private Double basePrice;

    /**
     * User trust score during negotiation
     * Helps AI learn user reliability
     */
    private Double trustScore;

    /**
     * Maximum discount allowed by rule-engine
     * Later replaced by AI prediction
     */
    private Double maxDiscount;

    /**
     * Explainable AI decision reason
     * VERY important for transparency & debugging
     */
    @Column(length = 500)
    private String decisionReason;

    // =========================
    // 📊 NEGOTIATION META
    // =========================
    @Enumerated(EnumType.STRING)
    private NegotiationStatus status;

    private Integer attemptNumber;

    private LocalDateTime createdAt;

    // 🔐 PRICE LOCK (IMPORTANT)
    private LocalDateTime lockExpiresAt;
    
    private Double aiConfidence;

}
