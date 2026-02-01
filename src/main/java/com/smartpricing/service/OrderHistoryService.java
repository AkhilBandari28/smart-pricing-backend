package com.smartpricing.service;

import com.smartpricing.dto.OrderHistoryDto;
import com.smartpricing.entity.Order;
import com.smartpricing.entity.Product;
import com.smartpricing.entity.User;
import com.smartpricing.repository.OrderRepository;
import com.smartpricing.repository.ProductRepository;
import com.smartpricing.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class OrderHistoryService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public OrderHistoryService(
            OrderRepository orderRepository,
            ProductRepository productRepository,
            UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    // 🔐 USER order history
    public Page<OrderHistoryDto> getUserOrders(
            Long userId,
            Pageable pageable) {

        return orderRepository.findByUserId(userId, pageable)
                .map(order -> map(order, false));
    }

    // 🔐 ADMIN order history
    public Page<OrderHistoryDto> getAllOrders(Pageable pageable) {

        return orderRepository.findAll(pageable)
                .map(order -> map(order, true));
    }

    // 🔄 Mapper
    private OrderHistoryDto map(Order order, boolean isAdmin) {

        Product product = productRepository
                .findById(order.getProductId())
                .orElse(null);

        OrderHistoryDto dto = new OrderHistoryDto();
        dto.setOrderId(order.getOrderId());
        dto.setProductId(order.getProductId());
        dto.setFinalPrice(order.getFinalPrice());
        dto.setPaymentStatus(order.getPaymentStatus().name());
        dto.setOrderedAt(order.getOrderedAt());

        if (product != null) {
            dto.setProductName(product.getName());
        }

        // ADMIN-only fields
        if (isAdmin) {
            User user = userRepository.findById(order.getUserId()).orElse(null);
            if (user != null) {
                dto.setUserId(user.getUserId());
                dto.setUserEmail(user.getEmail());
            }
        }

        return dto;
    }
}
