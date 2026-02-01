package com.smartpricing.dto;

import lombok.Data;

@Data
public class PaymentResponseDto {
    private String gatewayOrderId;
    private Double amount;
    private String status;
}
