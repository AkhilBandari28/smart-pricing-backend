package com.smartpricing.repository;

import com.smartpricing.entity.TrustScoreHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrustScoreHistoryRepository
        extends JpaRepository<TrustScoreHistory, Long> {

    List<TrustScoreHistory> findByUserIdOrderByCreatedAtDesc(Long userId);
}
