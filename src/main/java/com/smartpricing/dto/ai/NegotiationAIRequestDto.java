package com.smartpricing.dto.ai;

import lombok.Data;

@Data
public class NegotiationAIRequestDto {

    private Long userId;
    private Long productId;

    private double basePrice;
    private double offeredPrice;
    private double trustScore;
    private int attemptNumber;
}
