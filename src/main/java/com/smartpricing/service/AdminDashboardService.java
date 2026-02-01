package com.smartpricing.service;

import com.smartpricing.dto.AdminDashboardStatsDto;
import com.smartpricing.dto.TopProductDto;
import com.smartpricing.entity.Order;
import com.smartpricing.entity.OrderStatus;
import com.smartpricing.entity.PaymentStatus;
import com.smartpricing.entity.Product;
import com.smartpricing.repository.OrderRepository;
import com.smartpricing.repository.ProductRepository;
import com.smartpricing.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminDashboardService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public AdminDashboardService(
            OrderRepository orderRepository,
            UserRepository userRepository) {

        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    // 📊 DASHBOARD STATS
    public AdminDashboardStatsDto getStats() {

        AdminDashboardStatsDto dto = new AdminDashboardStatsDto();

        dto.setTotalOrders(orderRepository.count());
        dto.setTotalUsers(userRepository.count());
        dto.setTotalRevenue(orderRepository.totalRevenue());

        // ✅ FIXED LOGIC
        dto.setCancelledOrders(
                orderRepository.countByOrderStatus(OrderStatus.CANCELLED)
        );

        dto.setPendingOrders(
                orderRepository.countByPaymentStatus(PaymentStatus.PENDING)
        );

        return dto;
    }

    // 🔥 TOP PRODUCTS (NO EXTRA DB CALLS)
    public List<TopProductDto> getTopProducts(int limit) {
        return orderRepository.topSellingProducts(Pageable.ofSize(limit));
    }

    // 📦 RECENT ORDERS
    public Page<Order> getRecentOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }
}
