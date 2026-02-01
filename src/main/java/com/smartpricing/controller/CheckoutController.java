package com.smartpricing.controller;

import com.smartpricing.dto.CheckoutRequestDto;
import com.smartpricing.dto.CheckoutResponseDto;
import com.smartpricing.service.CheckoutService;
import com.smartpricing.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/checkout")
@Tag(name = "Checkout APIs", description = "Order checkout and order creation")
public class CheckoutController {

    private final CheckoutService checkoutService;

    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @Operation(
        summary = "Checkout product",
        description = "Creates an order after successful negotiation"
    )
    @PostMapping
    public ApiResponse<CheckoutResponseDto> checkout(
            @Valid @RequestBody CheckoutRequestDto request,
            Authentication authentication
    ) {
        CheckoutResponseDto response =
                checkoutService.checkout(request, authentication);

        return new ApiResponse<>(
                201,
                "Order created successfully",
                response
        );
    }
}
