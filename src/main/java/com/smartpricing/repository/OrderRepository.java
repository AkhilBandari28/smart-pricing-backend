//package com.smartpricing.repository;
//
//import com.smartpricing.dto.TopProductDto;
//import com.smartpricing.entity.Order;
//import com.smartpricing.entity.OrderStatus;
//import com.smartpricing.entity.PaymentStatus;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//
//import java.util.List;
//
//public interface OrderRepository extends JpaRepository<Order, Long> {
//
//    // 🔐 USER
//    Page<Order> findByUserId(Long userId, Pageable pageable);
//
//    // 📊 ADMIN COUNTS
//    long countByOrderStatus(OrderStatus status);
//
//    long countByPaymentStatus(PaymentStatus status);
//
//    // 💰 TOTAL REVENUE
//    @Query("""
//        SELECT COALESCE(SUM(o.finalPrice), 0)
//        FROM Order o
//        WHERE o.paymentStatus = 'SUCCESS'
//    """)
//    Double totalRevenue();
//
//    // 🔥 TOP PRODUCTS (OPTIMIZED)
//    @Query("""
//        SELECT new com.smartpricing.dto.TopProductDto(
//            p.productId,
//            p.name,
//            COUNT(o.orderId)
//        )
//        FROM Order o
//        JOIN Product p ON p.productId = o.productId
//        WHERE o.paymentStatus = 'SUCCESS'
//        GROUP BY p.productId, p.name
//        ORDER BY COUNT(o.orderId) DESC
//    """)
//    List<TopProductDto> topSellingProducts(Pageable pageable);
//}


//===================c;uade ai



package com.smartpricing.repository;

import com.smartpricing.dto.TopProductDto;
import com.smartpricing.entity.Order;
import com.smartpricing.entity.OrderStatus;
import com.smartpricing.entity.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // 🔐 USER - Paginated
    Page<Order> findByUserId(Long userId, Pageable pageable);

    // 🔥 NEW: USER - Get all orders sorted by date (for /my endpoint)
    List<Order> findByUserIdOrderByOrderedAtDesc(Long userId);

    // 📊 ADMIN COUNTS
    long countByOrderStatus(OrderStatus status);
    long countByPaymentStatus(PaymentStatus status);

    // 💰 TOTAL REVENUE
    @Query("""
        SELECT COALESCE(SUM(o.finalPrice), 0)
        FROM Order o
        WHERE o.paymentStatus = 'SUCCESS'
    """)
    Double totalRevenue();

    // 🔥 TOP PRODUCTS (OPTIMIZED)
    @Query("""
        SELECT new com.smartpricing.dto.TopProductDto(
            p.productId,
            p.name,
            COUNT(o.orderId)
        )
        FROM Order o
        JOIN Product p ON p.productId = o.productId
        WHERE o.paymentStatus = 'SUCCESS'
        GROUP BY p.productId, p.name
        ORDER BY COUNT(o.orderId) DESC
    """)
    List<TopProductDto> topSellingProducts(Pageable pageable);
    
    List<Order> findTop5ByOrderByCreatedAtDesc();

}
