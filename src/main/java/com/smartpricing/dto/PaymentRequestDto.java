package com.smartpricing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentRequestDto {

    @NotNull
    @Schema(example = "2", description = "Order ID")
    private Long orderId;
}

