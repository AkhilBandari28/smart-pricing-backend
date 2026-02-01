package com.smartpricing.intelligence;

import com.smartpricing.entity.NegotiationStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NegotiationResult {

    private NegotiationStatus status;
    private Double finalPrice;
    private String message;

    public static NegotiationResult accept(Double price, String msg) {
        return new NegotiationResult(NegotiationStatus.ACCEPTED, price, msg);
    }

    public static NegotiationResult counter(Double price, String msg) {
        return new NegotiationResult(NegotiationStatus.COUNTER, price, msg);
    }

    public static NegotiationResult reject(String msg) {
        return new NegotiationResult(NegotiationStatus.REJECTED, null, msg);
    }
}
