package com.smartpricing.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "refunds")
@Data
public class Refund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long refundId;

    private Long orderId;
    private Long userId;
    private Double amount;

    private String refundReferenceId;

    private LocalDateTime refundedAt;
}
