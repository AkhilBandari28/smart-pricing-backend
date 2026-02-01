package com.smartpricing.controller;

import com.smartpricing.dto.AiDecisionStatsDto;
import com.smartpricing.dto.AdminStatsResponse;
import com.smartpricing.dto.AiBiasDriftReportDto;
import com.smartpricing.entity.Product;
import com.smartpricing.entity.Order;
import com.smartpricing.repository.ProductRepository;
import com.smartpricing.repository.OrderRepository;
import com.smartpricing.service.AiMetricsService;
import com.smartpricing.service.AiBiasDriftService;
import com.smartpricing.util.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AiMetricsService aiMetricsService;
    private final AiBiasDriftService aiBiasDriftService;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public AdminController(
            AiMetricsService aiMetricsService,
            AiBiasDriftService aiBiasDriftService,
            ProductRepository productRepository,
            OrderRepository orderRepository
    ) {
        this.aiMetricsService = aiMetricsService;
        this.aiBiasDriftService = aiBiasDriftService;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    // =====================================================
    // 🔹 TEST ENDPOINT
    // =====================================================
    @GetMapping("/hello")
    public String adminTest() {
        return "Admin controller working!";
    }

    // =====================================================
    // 🤖 AI METRICS (FRONTEND EXPECTS THIS)
    // GET /api/admin/ai/metrics
    // =====================================================
    @GetMapping("/ai/metrics")
    public ApiResponse<AiDecisionStatsDto> getAiMetrics() {
        return new ApiResponse<>(
                200,
                "AI metrics",
                aiMetricsService.getDecisionStats()
        );
    }

    // =====================================================
    // 🤖 AI DECISION STATS (KEEPING OLD ENDPOINT)
    // GET /api/admin/ai/decision-stats
    // =====================================================
    @GetMapping("/ai/decision-stats")
    public ApiResponse<AiDecisionStatsDto> getAiDecisionStats() {
        return new ApiResponse<>(
                200,
                "AI decision metrics",
                aiMetricsService.getDecisionStats()
        );
    }

    // =====================================================
    // 📈 AI DECISION TREND (FIXES data.map ERROR)
    // GET /api/admin/ai/decision-trend
    // =====================================================
    @GetMapping("/ai/decision-trend")
    public ApiResponse<List<Map<String, Object>>> getAiDecisionTrend() {

        /*
         * Frontend expects an ARRAY like:
         * [
         *   { "label": "ACCEPT", "count": 10 },
         *   { "label": "REJECT", "count": 5 },
         *   { "label": "COUNTER", "count": 3 }
         * ]
         */

        List<Map<String, Object>> trend =
                aiMetricsService.getDecisionTrend();

        return new ApiResponse<>(
                200,
                "AI decision trend",
                trend != null ? trend : Collections.emptyList()
        );
    }

    // =====================================================
    // 🧠 AI HEALTH
    // GET /api/admin/ai/health
    // =====================================================
    @GetMapping("/ai/health")
    public ApiResponse<AiBiasDriftReportDto> getAiHealthReport() {
        return new ApiResponse<>(
                200,
                "AI bias & drift analysis",
                aiBiasDriftService.analyzeAiHealth()
        );
    }

    // =====================================================
    // 📦 TOP PRODUCTS
    // GET /api/admin/products/top
    // =====================================================
    @GetMapping("/products/top")
    public ApiResponse<List<Product>> getTopProducts() {
        List<Product> products = productRepository.findTop5ByOrderByStockDesc();
        return new ApiResponse<>(
                200,
                "Top products",
                products != null ? products : Collections.emptyList()
        );
    }

    // =====================================================
    // 🧾 RECENT ORDERS
    // GET /api/admin/orders/recent
    // =====================================================
    @GetMapping("/orders/recent")
    public ApiResponse<List<Order>> getRecentOrders() {
        List<Order> orders = orderRepository.findTop5ByOrderByCreatedAtDesc();
        return new ApiResponse<>(
                200,
                "Recent orders",
                orders != null ? orders : Collections.emptyList()
        );
    }

    // =====================================================
    // 📊 ADMIN DASHBOARD STATS
    // GET /api/admin/stats
    // =====================================================
    @GetMapping("/stats")
    public ApiResponse<AdminStatsResponse> getAdminStats() {

        AdminStatsResponse stats = new AdminStatsResponse(
                productRepository.count(),
                orderRepository.count()
        );

        return new ApiResponse<>(
                200,
                "Admin dashboard stats",
                stats
        );
    }
}
