package com.smartpricing.service;

import com.smartpricing.dto.CheckoutRequestDto;
import com.smartpricing.dto.CheckoutResponseDto;
import com.smartpricing.entity.Order;
import com.smartpricing.entity.OrderStatus;
import com.smartpricing.entity.PaymentStatus;
import com.smartpricing.entity.Product;
import com.smartpricing.entity.User;
import com.smartpricing.exception.BusinessException;
import com.smartpricing.repository.OrderRepository;
import com.smartpricing.repository.ProductRepository;
import com.smartpricing.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CheckoutService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CheckoutService(
            OrderRepository orderRepository,
            ProductRepository productRepository,
            UserRepository userRepository
    ) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional // ✅ Order + stock update must succeed together
    public CheckoutResponseDto checkout(
            CheckoutRequestDto request,
            Authentication authentication
    ) {
        // 🔐 Logged-in user
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("User not found"));

        // 📦 Product validation
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new BusinessException("Product not found"));

        if (product.getStock() <= 0) {
            throw new BusinessException("Product is out of stock");
        }

        // 🧾 Create order
        Order order = new Order();

        // ✅ FIX: Use IDs (matches your Order entity)
        order.setUserId(user.getUserId());        // 🔥 FIXED
        order.setProductId(product.getProductId()); // 🔥 FIXED

        order.setFinalPrice(request.getFinalPrice());
        order.setOrderStatus(OrderStatus.CREATED);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setOrderedAt(LocalDateTime.now());

        // 📉 Reduce stock
        product.setStock(product.getStock() - 1);

        // 💾 Save
        Order savedOrder = orderRepository.save(order);
        productRepository.save(product);

        // ✅ Response
        return new CheckoutResponseDto(
                savedOrder.getOrderId(),
                savedOrder.getFinalPrice()
        );
    }
}
