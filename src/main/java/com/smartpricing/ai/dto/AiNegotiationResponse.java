package com.smartpricing.ai.dto;

import lombok.Data;

@Data
public class AiNegotiationResponse {

    private String decision;          // ACCEPT | REJECT | COUNTER
    private Double finalPrice;         // nullable
    private double confidenceScore;    // Decision confidence (0–1)
    private String explanation;

    // ✅ NEW (UX clarity)
    private double offerQualityScore;  // 0–100 (% of base price)
}
