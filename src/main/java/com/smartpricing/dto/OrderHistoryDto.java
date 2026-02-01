package com.smartpricing.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OrderHistoryDto {

    private Long orderId;

    private Long productId;
    private String productName;
    private Double finalPrice;

    // ADMIN only
    private Long userId;
    private String userEmail;

    private String paymentStatus;
    private LocalDateTime orderedAt;
}
