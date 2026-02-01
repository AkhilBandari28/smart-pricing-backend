package com.smartpricing.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    private Long userId;
    private Long productId;
    private Double finalPrice;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", length = 30)
    private OrderStatus orderStatus;

    // =====================================================
    // 🧾 BUSINESS TIME
    // =====================================================
    @Column(nullable = false)
    private LocalDateTime orderedAt;

    // =====================================================
    // 🛠 SYSTEM TIME (ADMIN / SORTING)
    // =====================================================
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // =====================================================
    // 🔄 AUTO TIMESTAMP
    // =====================================================
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;

        if (this.orderedAt == null) {
            this.orderedAt = now;
        }
    }
}
