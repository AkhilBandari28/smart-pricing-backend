package com.smartpricing.ai.dto;

import lombok.Data;

@Data
public class AiNegotiationRequest {

    private Long userId;
    private Long productId;

    private Double basePrice;
    private Double offeredPrice;

    private Double trustScore;
    private Integer attemptNumber;
}
