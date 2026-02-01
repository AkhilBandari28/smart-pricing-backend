package com.smartpricing.service;

import com.smartpricing.dto.CancelOrderRequestDto;
import com.smartpricing.dto.CancelOrderResponseDto;
import com.smartpricing.entity.*;
import com.smartpricing.exception.BusinessException;
import com.smartpricing.repository.OrderRepository;
import com.smartpricing.repository.RefundRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class OrderCancelService {

    private final OrderRepository orderRepository;
    private final RefundRepository refundRepository;

    public OrderCancelService(
            OrderRepository orderRepository,
            RefundRepository refundRepository) {
        this.orderRepository = orderRepository;
        this.refundRepository = refundRepository;
    }

    public CancelOrderResponseDto cancelOrder(
            CancelOrderRequestDto request,
            Long userId) {

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new BusinessException("Order not found"));

        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("Unauthorized order cancellation");
        }

        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new BusinessException("Order already cancelled");
        }

        if (refundRepository.existsByOrderId(order.getOrderId())) {
            throw new BusinessException("Refund already processed");
        }

        if (order.getPaymentStatus() != PaymentStatus.SUCCESS) {
            throw new BusinessException("Only paid orders can be cancelled");
        }

        // 🔁 REFUND (MOCK)
        String refundRef = "refund_" + UUID.randomUUID();

        Refund refund = new Refund();
        refund.setOrderId(order.getOrderId());
        refund.setUserId(order.getUserId());
        refund.setAmount(order.getFinalPrice());
        refund.setRefundReferenceId(refundRef);
        refund.setRefundedAt(LocalDateTime.now());

        refundRepository.save(refund);

        // 🔄 UPDATE ORDER
        order.setOrderStatus(OrderStatus.CANCELLED);
        order.setPaymentStatus(PaymentStatus.REFUNDED);
        orderRepository.save(order);

        // 🔁 RESPONSE
        CancelOrderResponseDto response = new CancelOrderResponseDto();
        response.setOrderId(order.getOrderId());
        response.setRefundedAmount(order.getFinalPrice());
        response.setRefundReferenceId(refundRef);
        response.setRefundedAt(refund.getRefundedAt());

        return response;
    }
}
