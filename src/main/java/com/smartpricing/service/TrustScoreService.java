package com.smartpricing.service;

import com.smartpricing.dto.TrustScoreHistoryDto;
import com.smartpricing.entity.TrustScoreHistory;
import com.smartpricing.entity.User;
import com.smartpricing.repository.TrustScoreHistoryRepository;
import com.smartpricing.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrustScoreService {

    private final UserRepository userRepository;
    private final TrustScoreHistoryRepository historyRepository;

    public TrustScoreService(
            UserRepository userRepository,
            TrustScoreHistoryRepository historyRepository
    ) {
        this.userRepository = userRepository;
        this.historyRepository = historyRepository;
    }

    // =====================================================
    // 🔥 STEP-9A: SINGLE SOURCE OF TRUTH (WRITE)
    // =====================================================
    @Transactional
    public void updateTrustScore(
            Long userId,
            double delta,
            String reason,
            Long referenceId
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        double oldScore = user.getTrustScore() != null
                ? user.getTrustScore()
                : 0.0;

        double newScore = Math.min(100, Math.max(0, oldScore + delta));

        // ✅ Update user score
        user.setTrustScore(newScore);
        userRepository.save(user);

        // ✅ Save audit history
        TrustScoreHistory history = new TrustScoreHistory();
        history.setUserId(userId);
        history.setOldScore(oldScore);
        history.setNewScore(newScore);
        history.setChangeAmount(delta); // 🔥 IMPORTANT
        history.setReason(reason);
        history.setReferenceId(referenceId);
        history.setCreatedAt(LocalDateTime.now());

        historyRepository.save(history);
    }

    // =====================================================
    // 🔎 STEP-9B: USER – View OWN trust history
    // =====================================================
    public List<TrustScoreHistoryDto> getUserHistory(Long userId) {

        return historyRepository
                .findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(h -> new TrustScoreHistoryDto(
                        h.getChangeAmount(),
                        h.getNewScore(),
                        h.getReason(),
                        h.getReferenceId(),
                        h.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    // =====================================================
    // 🛡 STEP-9B: ADMIN – View ANY user trust history
    // =====================================================
    public List<TrustScoreHistoryDto> getHistoryByUser(Long userId) {
        return getUserHistory(userId); // reuse logic
    }

    // =====================================================
    // 🔑 HELPER: Resolve userId from email (Controller use)
    // =====================================================
    public Long getUserIdByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getUserId();
    }
}
