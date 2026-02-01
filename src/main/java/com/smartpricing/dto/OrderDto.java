package com.smartpricing.dto;

import lombok.Data;

@Data
public class OrderDto {

    private Long orderId;
    private Long negotiationId;
    private Double finalPrice;
    private String paymentStatus;
}
