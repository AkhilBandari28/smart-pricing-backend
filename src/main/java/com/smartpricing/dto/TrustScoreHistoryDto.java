package com.smartpricing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TrustScoreHistoryDto {

    private Double changeAmount;
    private Double newScore;
    private String reason;
    private Long referenceId;
    private LocalDateTime createdAt;
}
