package com.smartpricing.controller;

import com.smartpricing.dto.CancelOrderRequestDto;
import com.smartpricing.dto.CancelOrderResponseDto;
import com.smartpricing.entity.User;
import com.smartpricing.exception.BusinessException;
import com.smartpricing.repository.UserRepository;
import com.smartpricing.service.OrderCancelService;
import com.smartpricing.util.ApiResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderCancelController {

    private final OrderCancelService orderCancelService;
    private final UserRepository userRepository;

    public OrderCancelController(
            OrderCancelService orderCancelService,
            UserRepository userRepository) {
        this.orderCancelService = orderCancelService;
        this.userRepository = userRepository;
    }

    @PostMapping("/cancel")
    public ApiResponse<CancelOrderResponseDto> cancelOrder(
            @RequestBody CancelOrderRequestDto request) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("User not found"));

        return new ApiResponse<>(
                200,
                "Order cancelled and refund processed",
                orderCancelService.cancelOrder(request, user.getUserId())
        );
    }
}
