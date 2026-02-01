package com.smartpricing.service;

import com.smartpricing.dto.AiDecisionStatsDto;
import com.smartpricing.entity.NegotiationStatus;
import com.smartpricing.repository.NegotiationRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AiMetricsService {

    private final NegotiationRepository negotiationRepository;

    public AiMetricsService(NegotiationRepository negotiationRepository) {
        this.negotiationRepository = negotiationRepository;
    }

    // =====================================================
    // 📊 AI DECISION SUMMARY STATS
    // =====================================================
    public AiDecisionStatsDto getDecisionStats() {

        long accepted = negotiationRepository.countByStatus(NegotiationStatus.ACCEPTED);
        long rejected = negotiationRepository.countByStatus(NegotiationStatus.REJECTED);
        long counter  = negotiationRepository.countByStatus(NegotiationStatus.COUNTER);

        Double avgConfidence = negotiationRepository.findAverageAiConfidence();
        if (avgConfidence == null) avgConfidence = 0.0;

        return new AiDecisionStatsDto(
                accepted,
                rejected,
                counter,
                Math.round(avgConfidence * 100.0) / 100.0
        );
    }

    // =====================================================
    // 📈 AI DECISION TREND (FIXES data.map ERROR)
    // =====================================================
    public List<Map<String, Object>> getDecisionTrend() {

        List<Map<String, Object>> trend = new ArrayList<>();

        trend.add(Map.of(
                "label", "ACCEPT",
                "count", negotiationRepository.countByStatus(NegotiationStatus.ACCEPTED)
        ));

        trend.add(Map.of(
                "label", "REJECT",
                "count", negotiationRepository.countByStatus(NegotiationStatus.REJECTED)
        ));

        trend.add(Map.of(
                "label", "COUNTER",
                "count", negotiationRepository.countByStatus(NegotiationStatus.COUNTER)
        ));

        return trend;
    }
}
