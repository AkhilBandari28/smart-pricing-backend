package com.smartpricing.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "trust_score_history")
@Data
public class TrustScoreHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Double oldScore;
    private Double newScore;
    
 // 🔥 ADD THIS FIELD (CAUSE OF ERROR)
    private Double changeAmount;

    private String reason;

    private Long referenceId;

    private LocalDateTime createdAt;
}
