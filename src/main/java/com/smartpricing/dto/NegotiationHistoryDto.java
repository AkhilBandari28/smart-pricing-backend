package com.smartpricing.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NegotiationHistoryDto {

    private Long negotiationId;
    private Long productId;
    private Double offeredPrice;
    private Double finalPrice;
    private String status;
    private Integer attemptNumber;
    private LocalDateTime createdAt;
}
