package com.smartpricing.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CancelOrderResponseDto {
    private Long orderId;
    private String refundReferenceId;
    private Double refundedAmount;
    private LocalDateTime refundedAt;
}
