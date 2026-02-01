package com.smartpricing.service;

import com.smartpricing.dto.AiBiasDriftReportDto;
import com.smartpricing.entity.NegotiationStatus;
import com.smartpricing.repository.NegotiationRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AiBiasDriftService {

    private final NegotiationRepository negotiationRepository;

    public AiBiasDriftService(NegotiationRepository negotiationRepository) {
        this.negotiationRepository = negotiationRepository;
    }

    public AiBiasDriftReportDto analyzeAiHealth() {

        AiBiasDriftReportDto report = new AiBiasDriftReportDto();
        List<String> warnings = new ArrayList<>();

        long total = negotiationRepository.count();
        if (total == 0) {
            report.setBiasDetected(false);
            report.setDriftDetected(false);
            report.setRecommendation("Not enough data yet");
            return report;
        }

        // =========================
        // BIAS DETECTION
        // =========================
        long lowTrustAccepted =
                negotiationRepository.countByTrustScoreLessThanAndStatus(
                        40.0, NegotiationStatus.ACCEPTED);

        long highTrustAccepted =
                negotiationRepository.countByTrustScoreGreaterThanAndStatus(
                        70.0, NegotiationStatus.ACCEPTED);

        long lowTrustTotal =
                negotiationRepository.countByTrustScoreLessThan(40.0);

        long highTrustTotal =
                negotiationRepository.countByTrustScoreGreaterThan(70.0);

        double lowTrustRate =
                lowTrustTotal == 0 ? 0 : (double) lowTrustAccepted / lowTrustTotal;

        double highTrustRate =
                highTrustTotal == 0 ? 0 : (double) highTrustAccepted / highTrustTotal;

        report.setLowTrustAcceptanceRate(lowTrustRate);
        report.setHighTrustAcceptanceRate(highTrustRate);

        if (highTrustRate - lowTrustRate > 0.4) {
            report.setBiasDetected(true);
            warnings.add("Significant acceptance bias between trust groups");
        } else {
            report.setBiasDetected(false);
        }

        // =========================
        // DRIFT DETECTION
        // =========================
        Double avgPriceRatio = negotiationRepository.findAveragePriceRatio();
        Double avgTrustScore = negotiationRepository.findAverageTrustScore();

        report.setAvgPriceRatio(avgPriceRatio != null ? avgPriceRatio : 0);
        report.setAvgTrustScore(avgTrustScore != null ? avgTrustScore : 0);

        if (avgPriceRatio != null && avgPriceRatio < 0.65) {
            report.setDriftDetected(true);
            warnings.add("Price ratio drift detected (too aggressive offers)");
        } else {
            report.setDriftDetected(false);
        }

        // =========================
        // RECOMMENDATION
        // =========================
        if (report.isBiasDetected() || report.isDriftDetected()) {
            report.setRecommendation("Retrain AI model");
        } else {
            report.setRecommendation("AI is stable");
        }

        report.setWarnings(warnings);
        return report;
    }
}
