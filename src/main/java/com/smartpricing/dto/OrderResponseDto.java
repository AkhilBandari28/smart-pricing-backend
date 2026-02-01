//package com.smartpricing.dto;
//
//import lombok.Data;
//
//import java.time.LocalDateTime;
//
//@Data
//public class OrderResponseDto {
//
//    private Long orderId;
//    private Long productId;
//    private Double finalPrice;
//    private String paymentStatus;
//    private LocalDateTime orderedAt;
//}


//==========================cluade ai


package com.smartpricing.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OrderResponseDto {
    private Long orderId;
    private Long productId;
    private String productName;       // 🔥 NEW: Product name for display
    private Double finalPrice;
    private Double originalPrice;     // 🔥 NEW: Original price to show savings
    private String paymentStatus;
    private String status;            // 🔥 NEW: Order status (CREATED, PAID, SHIPPED, etc.)
    private LocalDateTime orderedAt;
}