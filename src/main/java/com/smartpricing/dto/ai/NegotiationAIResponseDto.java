package com.smartpricing.dto.ai;

import lombok.Data;

@Data
public class NegotiationAIResponseDto {

    private String decision;          // ACCEPT | REJECT | COUNTER
    private Double finalPrice;         // nullable
    private double confidenceScore;
    private String explanation;
}
