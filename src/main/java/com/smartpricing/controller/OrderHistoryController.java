package com.smartpricing.controller;

import com.smartpricing.dto.OrderHistoryDto;
import com.smartpricing.entity.User;
import com.smartpricing.exception.BusinessException;
import com.smartpricing.repository.UserRepository;
import com.smartpricing.service.OrderHistoryService;
import com.smartpricing.util.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders/history")
public class OrderHistoryController {

    private final OrderHistoryService orderHistoryService;
    private final UserRepository userRepository;

    public OrderHistoryController(
            OrderHistoryService orderHistoryService,
            UserRepository userRepository) {
        this.orderHistoryService = orderHistoryService;
        this.userRepository = userRepository;
    }

    // =========================
    // 🔐 USER ORDER HISTORY
    // =========================
    @Operation(
    	    summary = "User order history",
    	    description = "Fetch logged-in user's order history with pagination"
    	)
    @GetMapping
    public ApiResponse<Page<OrderHistoryDto>> getMyOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("User not found"));

        return new ApiResponse<>(
                200,
                "User order history fetched",
                orderHistoryService.getUserOrders(
                        user.getUserId(),
                        PageRequest.of(page, size)
                )
        );
    }

    // =========================
    // 🔐 ADMIN ORDER HISTORY
    // =========================
    @Operation(
    	    summary = "Admin order history",
    	    description = "ADMIN only – view all orders with pagination"
    	)
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/admin")
    public ApiResponse<Page<OrderHistoryDto>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return new ApiResponse<>(
                200,
                "All orders fetched (ADMIN)",
                orderHistoryService.getAllOrders(
                        PageRequest.of(page, size)
                )
        );
    }
}
