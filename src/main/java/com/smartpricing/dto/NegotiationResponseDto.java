package com.smartpricing.dto;

import lombok.Data;

@Data
public class NegotiationResponseDto {

    private String status;
    private Double finalPrice;
    private String message;

    // ✅ ADD THIS
    private Integer attemptNumber;
}
