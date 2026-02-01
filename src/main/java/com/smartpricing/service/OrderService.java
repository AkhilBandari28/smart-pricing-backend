//package com.smartpricing.service;
//
//import com.smartpricing.dto.CheckoutRequestDto;
//import com.smartpricing.dto.OrderResponseDto;
//import com.smartpricing.entity.*;
//import com.smartpricing.exception.BusinessException;
//import com.smartpricing.repository.NegotiationRepository;
//import com.smartpricing.repository.OrderRepository;
//import com.smartpricing.repository.ProductRepository;
//import com.smartpricing.repository.UserRepository;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//
//@Service
//public class OrderService {
//
//    private final UserRepository userRepository;
//    private final ProductRepository productRepository;
//    private final NegotiationRepository negotiationRepository;
//    private final OrderRepository orderRepository;
//
//    public OrderService(
//            UserRepository userRepository,
//            ProductRepository productRepository,
//            NegotiationRepository negotiationRepository,
//            OrderRepository orderRepository) {
//        this.userRepository = userRepository;
//        this.productRepository = productRepository;
//        this.negotiationRepository = negotiationRepository;
//        this.orderRepository = orderRepository;
//    }
//
////    public OrderResponseDto checkout(
////            CheckoutRequestDto request,
////            String userEmail) {
////
////        User user = userRepository.findByEmail(userEmail)
////                .orElseThrow(() -> new BusinessException("User not found"));
////
////        Product product = productRepository.findById(request.getProductId())
////                .orElseThrow(() -> new BusinessException("Product not found"));
////
////        // 🔐 FETCH ACTIVE PRICE LOCK
////        Negotiation negotiation = negotiationRepository
////                .findFirstByUserIdAndProductIdAndStatusAndLockExpiresAtAfter(
////                        user.getUserId(),
////                        product.getProductId(),
////                        NegotiationStatus.ACCEPTED,
////                        LocalDateTime.now()
////                )
////                .orElseThrow(() ->
////                        new BusinessException("No active price lock found"));
////
////        // 🛒 CREATE ORDER (PAYMENT PENDING)
////        Order order = new Order();
////        order.setUserId(user.getUserId());
////        order.setProductId(product.getProductId());
////        order.setFinalPrice(negotiation.getFinalPrice());
////        order.setPaymentStatus(PaymentStatus.PENDING);
////        order.setOrderedAt(LocalDateTime.now());
////
////        Order savedOrder = orderRepository.save(order);
////
////        // ❌ DO NOT CLOSE NEGOTIATION HERE
////        // negotiation.setStatus(COMPLETED) ❌ WRONG PLACE
////
////        // 🔁 RESPONSE
////        OrderResponseDto response = new OrderResponseDto();
////        response.setOrderId(savedOrder.getOrderId());
////        response.setProductId(savedOrder.getProductId());
////        response.setFinalPrice(savedOrder.getFinalPrice());
////        response.setPaymentStatus(savedOrder.getPaymentStatus().name());
////        response.setOrderedAt(savedOrder.getOrderedAt());
////
////        return response;
////    }
//    
//    // newly added-----by cluade ai----------
//    public OrderResponseDto checkout(
//            CheckoutRequestDto request,
//            String userEmail) {
//        User user = userRepository.findByEmail(userEmail)
//                .orElseThrow(() -> new BusinessException("User not found"));
//
//        Product product = productRepository.findById(request.getProductId())
//                .orElseThrow(() -> new BusinessException("Product not found"));
//
//        // 🔐 FETCH ACTIVE PRICE LOCK
//        Negotiation negotiation = negotiationRepository
//                .findFirstByUserIdAndProductIdAndStatusAndLockExpiresAtAfter(
//                        user.getUserId(),
//                        product.getProductId(),
//                        NegotiationStatus.ACCEPTED,
//                        LocalDateTime.now()
//                )
//                .orElseThrow(() ->
//                        new BusinessException("No active price lock found"));
//
//        // 🛒 CREATE ORDER (PAYMENT PENDING)
//        Order order = new Order();
//        order.setUserId(user.getUserId());
//        order.setProductId(product.getProductId());
//        order.setFinalPrice(negotiation.getFinalPrice());
//        order.setPaymentStatus(PaymentStatus.PENDING);
//        order.setOrderStatus(OrderStatus.CREATED);  // 🔥 ADD THIS
//        order.setOrderedAt(LocalDateTime.now());
//
//        Order savedOrder = orderRepository.save(order);
//        orderRepository.flush();  // 🔥 ADD THIS - Ensures ID is generated
//
//        // 🔁 RESPONSE
//        OrderResponseDto response = new OrderResponseDto();
//        response.setOrderId(savedOrder.getOrderId());
//        response.setProductId(savedOrder.getProductId());
//        response.setFinalPrice(savedOrder.getFinalPrice());
//        response.setPaymentStatus(savedOrder.getPaymentStatus().name());
//        response.setOrderedAt(savedOrder.getOrderedAt());
//        
//        return response;
//    }
//    
//    public void cancelOrder(Long orderId, String email) {
//
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new BusinessException("User not found"));
//
//        Order order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new BusinessException("Order not found"));
//
//        if (!order.getUserId().equals(user.getUserId())) {
//            throw new BusinessException("Unauthorized cancellation");
//        }
//
//        if (order.getPaymentStatus() != PaymentStatus.SUCCESS) {
//            throw new BusinessException("Order not paid");
//        }
//
//        order.setPaymentStatus(PaymentStatus.REFUNDED);
//        orderRepository.save(order);
//    }
//
//}


//===================================cluade ai


package com.smartpricing.service;

import com.smartpricing.dto.CheckoutRequestDto;
import com.smartpricing.dto.OrderResponseDto;
import com.smartpricing.entity.*;
import com.smartpricing.exception.BusinessException;
import com.smartpricing.repository.NegotiationRepository;
import com.smartpricing.repository.OrderRepository;
import com.smartpricing.repository.ProductRepository;
import com.smartpricing.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final NegotiationRepository negotiationRepository;
    private final OrderRepository orderRepository;

    public OrderService(
            UserRepository userRepository,
            ProductRepository productRepository,
            NegotiationRepository negotiationRepository,
            OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.negotiationRepository = negotiationRepository;
        this.orderRepository = orderRepository;
    }

    public OrderResponseDto checkout(
            CheckoutRequestDto request,
            String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BusinessException("User not found"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new BusinessException("Product not found"));

        // 🔐 FETCH ACTIVE PRICE LOCK
        Negotiation negotiation = negotiationRepository
                .findFirstByUserIdAndProductIdAndStatusAndLockExpiresAtAfter(
                        user.getUserId(),
                        product.getProductId(),
                        NegotiationStatus.ACCEPTED,
                        LocalDateTime.now()
                )
                .orElseThrow(() ->
                        new BusinessException("No active price lock found"));

        // 🛒 CREATE ORDER (PAYMENT PENDING)
        Order order = new Order();
        order.setUserId(user.getUserId());
        order.setProductId(product.getProductId());
        order.setFinalPrice(negotiation.getFinalPrice());
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setOrderStatus(OrderStatus.CREATED);
        order.setOrderedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);
        orderRepository.flush(); // 🔥 Ensures ID is generated immediately

        // 🔁 RESPONSE
        return mapToDto(savedOrder, product);
    }

    // 🔥 NEW: Get all orders for a user
    public List<OrderResponseDto> getOrdersByUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("User not found"));

        List<Order> orders = orderRepository.findByUserIdOrderByOrderedAtDesc(user.getUserId());

        return orders.stream()
                .map(order -> {
                    Product product = productRepository.findById(order.getProductId()).orElse(null);
                    return mapToDto(order, product);
                })
                .collect(Collectors.toList());
    }

    // 🔥 NEW: Get single order by ID
    public OrderResponseDto getOrderById(Long orderId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("User not found"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("Order not found"));

        // Check ownership
        if (!order.getUserId().equals(user.getUserId())) {
            throw new BusinessException("Unauthorized access to order");
        }

        Product product = productRepository.findById(order.getProductId()).orElse(null);
        return mapToDto(order, product);
    }

    public void cancelOrder(Long orderId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("User not found"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("Order not found"));

        if (!order.getUserId().equals(user.getUserId())) {
            throw new BusinessException("Unauthorized cancellation");
        }

        if (order.getPaymentStatus() == PaymentStatus.SUCCESS) {
            order.setPaymentStatus(PaymentStatus.REFUNDED);
            order.setOrderStatus(OrderStatus.CANCELLED);
        } else {
            order.setOrderStatus(OrderStatus.CANCELLED);
        }
        
        orderRepository.save(order);
    }

    // 🔥 Helper method to map Order to DTO
    private OrderResponseDto mapToDto(Order order, Product product) {
        OrderResponseDto dto = new OrderResponseDto();
        dto.setOrderId(order.getOrderId());
        dto.setProductId(order.getProductId());
        dto.setFinalPrice(order.getFinalPrice());
        dto.setPaymentStatus(order.getPaymentStatus() != null ? order.getPaymentStatus().name() : null);
        dto.setOrderedAt(order.getOrderedAt());
        
        // Add product details if available
        if (product != null) {
            dto.setProductName(product.getName());
            dto.setOriginalPrice(product.getBasePrice());  // 🔥 FIXED: was getPrice()
        }
        
        // Add order status
        if (order.getOrderStatus() != null) {
            dto.setStatus(order.getOrderStatus().name());
        }
        
        return dto;
    }
}
