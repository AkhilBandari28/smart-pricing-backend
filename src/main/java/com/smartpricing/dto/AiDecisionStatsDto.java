package com.smartpricing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AiDecisionStatsDto {

    private long acceptedCount;
    private long rejectedCount;
    private long counterCount;

    private double averageConfidence;
}
