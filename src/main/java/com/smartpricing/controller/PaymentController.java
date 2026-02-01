package com.smartpricing.controller;

import com.smartpricing.dto.*;
import com.smartpricing.service.PaymentService;
import com.smartpricing.util.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // 🔹 Step 1: Create payment
    @Operation(
    	    summary = "Create payment",
    	    description = "Creates payment gateway order (Razorpay-style)"
    	)
    @PostMapping("/create")
    public ApiResponse<PaymentResponseDto> createPayment(
            @RequestBody PaymentRequestDto request) {

        return new ApiResponse<>(
                200,
                "Payment initiated",
                paymentService.createPayment(request)
        );
    }

    // 🔹 Step 2: Verify payment
    @Operation(
    	    summary = "Verify payment",
    	    description = "Verify payment and confirm order"
    	)
    @PostMapping("/verify")
    public ApiResponse<String> verifyPayment(
            @RequestBody PaymentVerificationDto request) {

        return new ApiResponse<>(
                200,
                "Payment verified",
                paymentService.verifyPayment(request)
        );
    }
}
