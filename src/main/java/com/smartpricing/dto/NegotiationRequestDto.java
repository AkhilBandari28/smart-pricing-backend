package com.smartpricing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NegotiationRequestDto {

    @NotNull
    @Schema(example = "5", description = "Product ID")
    private Long productId;

    @NotNull
    @Min(1)
    @Schema(example = "78000", description = "User offered price")
    private Double offeredPrice;
}
