package com.smartpricing.repository;

import com.smartpricing.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefundRepository extends JpaRepository<Refund, Long> {
    boolean existsByOrderId(Long orderId);
}
