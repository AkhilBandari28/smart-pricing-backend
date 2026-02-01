package com.smartpricing.service;

import com.smartpricing.dto.*;
import com.smartpricing.entity.*;
import com.smartpricing.exception.BusinessException;
import com.smartpricing.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final NegotiationRepository negotiationRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final TrustScoreService trustScoreService; // 🔥 NEW

    public PaymentService(
            OrderRepository orderRepository,
            PaymentRepository paymentRepository,
            NegotiationRepository negotiationRepository,
            ProductRepository productRepository,
            UserRepository userRepository,
            TrustScoreService trustScoreService
    ) {
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.negotiationRepository = negotiationRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.trustScoreService = trustScoreService;
    }

    // ================= CREATE PAYMENT =================
    public PaymentResponseDto createPayment(PaymentRequestDto request) {

    	// 🔥 ADD NULL CHECK
        if (request.getOrderId() == null) {
            throw new BusinessException("Order ID is required");
        }

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new BusinessException("Order not found with ID: " + request.getOrderId()));
    	
    	
//        Order order = orderRepository.findById(request.getOrderId())
//                .orElseThrow(() -> new BusinessException("Order not found"));

        if (order.getPaymentStatus() == PaymentStatus.SUCCESS) {
            throw new BusinessException("Order already paid");
        }

        paymentRepository.findByOrderId(order.getOrderId())
                .ifPresent(p -> { throw new BusinessException("Payment already initiated"); });

        String gatewayOrderId = "order_" + UUID.randomUUID();

        Payment payment = new Payment();
        payment.setOrderId(order.getOrderId());
        payment.setUserId(order.getUserId());
        payment.setAmount(order.getFinalPrice());
        payment.setStatus(PaymentStatus.CREATED);
        payment.setPaymentGatewayOrderId(gatewayOrderId);
        payment.setCreatedAt(LocalDateTime.now());

        paymentRepository.save(payment);

        PaymentResponseDto response = new PaymentResponseDto();
        response.setGatewayOrderId(gatewayOrderId);
        response.setAmount(payment.getAmount());
        response.setStatus(payment.getStatus().name());

        return response;
    }

    // ================= VERIFY PAYMENT =================
    @Transactional
    public String verifyPayment(PaymentVerificationDto request) {

        Payment payment = paymentRepository
                .findByPaymentGatewayOrderId(request.getGatewayOrderId())
                .orElseThrow(() -> new BusinessException("Invalid payment order"));

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            return "Payment already verified";
        }

        payment.setPaymentGatewayPaymentId(request.getGatewayPaymentId());
        payment.setStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(payment);

        Order order = orderRepository.findById(payment.getOrderId())
                .orElseThrow(() -> new BusinessException("Order not found"));

        order.setPaymentStatus(PaymentStatus.SUCCESS);
        order.setOrderStatus(OrderStatus.PAID);
        orderRepository.save(order);

        Negotiation negotiation = negotiationRepository
                .findFirstByUserIdAndProductIdAndStatus(
                        order.getUserId(),
                        order.getProductId(),
                        NegotiationStatus.ACCEPTED
                )
                .orElseThrow(() -> new BusinessException("Negotiation not found"));

        negotiation.setStatus(NegotiationStatus.COMPLETED);
        negotiationRepository.save(negotiation);

        Product product = productRepository.findById(order.getProductId())
                .orElseThrow(() -> new BusinessException("Product not found"));

        if (product.getStock() <= 0) {
            throw new BusinessException("Product out of stock");
        }

        product.setStock(product.getStock() - 1);
        productRepository.save(product);

        // 🔥 TRUST +5 (MOST IMPORTANT SIGNAL)
        User user = userRepository.findById(order.getUserId()).orElseThrow();
        trustScoreService.updateTrustScore(
        	    order.getUserId(),
        	    5,
        	    "PAYMENT_SUCCESS",
        	    order.getOrderId()
        	);


        return "Payment successful. Order marked as PAID.";
    }
}
