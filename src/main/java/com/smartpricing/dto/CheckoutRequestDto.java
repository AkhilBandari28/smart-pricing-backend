package com.smartpricing.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CheckoutRequestDto {

    @NotNull
    private Long productId;

    @NotNull
    private Double finalPrice;
}
