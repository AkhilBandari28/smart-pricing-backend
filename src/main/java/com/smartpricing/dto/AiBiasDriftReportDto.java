package com.smartpricing.dto;

import lombok.Data;

import java.util.List;

@Data
public class AiBiasDriftReportDto {

    // Bias
    private boolean biasDetected;
    private double lowTrustAcceptanceRate;
    private double highTrustAcceptanceRate;

    // Drift
    private boolean driftDetected;
    private double avgPriceRatio;
    private double avgTrustScore;

    // Meta
    private List<String> warnings;
    private String recommendation;
}
