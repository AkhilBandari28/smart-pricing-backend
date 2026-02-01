package com.smartpricing.controller;

import com.smartpricing.dto.CheckoutRequestDto;
import com.smartpricing.dto.OrderResponseDto;
import com.smartpricing.entity.Order;
import com.smartpricing.repository.OrderRepository;
import com.smartpricing.service.OrderService;
import com.smartpricing.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Order management APIs")
public class OrderController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;

    // ✅ Constructor Injection
    public OrderController(
            OrderService orderService,
            OrderRepository orderRepository
    ) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
    }

    // =====================================================
    // 🛒 CHECKOUT ORDER
    // POST /api/orders/checkout
    // =====================================================
    @Operation(
            summary = "Checkout order",
            description = "Create order after successful negotiation price lock. Payment status is PENDING."
    )
    @PostMapping("/checkout")
    public ApiResponse<OrderResponseDto> checkout(
            @RequestBody CheckoutRequestDto request
    ) {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        OrderResponseDto response =
                orderService.checkout(request, email);

        return new ApiResponse<>(
                201,
                "Order placed successfully",
                response
        );
    }

    // =====================================================
    // 📦 GET MY ORDERS (USER)
    // GET /api/orders/my
    // =====================================================
    @Operation(
            summary = "Get my orders",
            description = "Fetch all orders of the currently logged-in user"
    )
    @GetMapping("/my")
    public ApiResponse<List<OrderResponseDto>> getMyOrders() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        List<OrderResponseDto> orders =
                orderService.getOrdersByUser(email);

        return new ApiResponse<>(
                200,
                "Orders fetched successfully",
                orders
        );
    }

    // =====================================================
    // 🧑‍💼 GET ALL ORDERS (ADMIN) 🔥 FIX
    // GET /api/orders
    // =====================================================
    @Operation(
            summary = "Get all orders (Admin)",
            description = "Fetch all orders in the system (Admin only)"
    )
    @GetMapping
    public ApiResponse<List<Order>> getAllOrders() {

        List<Order> orders = orderRepository.findAll();

        return new ApiResponse<>(
                200,
                "All orders",
                orders
        );
    }

    // =====================================================
    // 📄 GET ORDER BY ID
    // GET /api/orders/{orderId}
    // =====================================================
    @Operation(
            summary = "Get order by ID",
            description = "Fetch single order details for the logged-in user"
    )
    @GetMapping("/{orderId}")
    public ApiResponse<OrderResponseDto> getOrderById(
            @PathVariable Long orderId
    ) {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        OrderResponseDto order =
                orderService.getOrderById(orderId, email);

        return new ApiResponse<>(
                200,
                "Order fetched successfully",
                order
        );
    }

    // =====================================================
    // ❌ CANCEL ORDER
    // DELETE /api/orders/{orderId}/cancel
    // =====================================================
    @Operation(
            summary = "Cancel order",
            description = "Cancel order and initiate refund if payment is completed"
    )
    @DeleteMapping("/{orderId}/cancel")
    public ApiResponse<String> cancelOrder(
            @PathVariable Long orderId
    ) {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        orderService.cancelOrder(orderId, email);

        return new ApiResponse<>(
                200,
                "Order cancelled",
                "Refund initiated successfully"
        );
    }
}
