package com.smartpricing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CheckoutResponseDto {

    private Long orderId;
    private Double amount;
}
