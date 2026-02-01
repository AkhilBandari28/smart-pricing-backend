package com.smartpricing.entity;

public enum OrderStatus {

    CREATED,        // Order created after checkout
    PAYMENT_PENDING,
    PAID,
    CONFIRMED,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    REFUNDED
}
