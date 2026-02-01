package com.smartpricing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentVerificationDto {

    @NotNull
    @Schema(example = "order_abc123")
    private String gatewayOrderId;

    @NotNull
    @Schema(example = "pay_test_987654")
    private String gatewayPaymentId;
}

