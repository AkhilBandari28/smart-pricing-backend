package com.smartpricing.controller;

import com.smartpricing.dto.AdminDashboardStatsDto;
import com.smartpricing.dto.TopProductDto;
import com.smartpricing.entity.Order;
import com.smartpricing.service.AdminDashboardService;
import com.smartpricing.util.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/dashboard")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    public AdminDashboardController(AdminDashboardService adminDashboardService) {
        this.adminDashboardService = adminDashboardService;
    }

    // ✅ ROOT DASHBOARD (FIX FOR YOUR ERROR)
    @GetMapping
    public ApiResponse<AdminDashboardStatsDto> dashboard() {
        return new ApiResponse<>(
                200,
                "Admin dashboard loaded",
                adminDashboardService.getStats()
        );
    }

    // 📊 STATS
    @Operation(
    	    summary = "Dashboard stats",
    	    description = "ADMIN dashboard metrics (orders, revenue, users)"
    	)
    @GetMapping("/stats")
    public ApiResponse<AdminDashboardStatsDto> stats() {
        return new ApiResponse<>(
                200,
                "Dashboard stats fetched",
                adminDashboardService.getStats()
        );
    }

    // 🔥 TOP PRODUCTS
    @Operation(
    	    summary = "Top selling products",
    	    description = "ADMIN – list top selling products"
    	)
    @GetMapping("/top-products")
    public ApiResponse<List<TopProductDto>> topProducts(
            @RequestParam(defaultValue = "5") int limit) {

        return new ApiResponse<>(
                200,
                "Top selling products",
                adminDashboardService.getTopProducts(limit)
        );
    }

    // 📦 RECENT ORDERS
    @Operation(
    	    summary = "Recent orders",
    	    description = "ADMIN – view recent orders with pagination"
    	)
    @GetMapping("/orders")
    public ApiResponse<Page<Order>> recentOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return new ApiResponse<>(
                200,
                "Recent orders",
                adminDashboardService.getRecentOrders(
                        PageRequest.of(page, size)
                )
        );
    }
}
