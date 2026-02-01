package com.smartpricing.controller;

import com.smartpricing.dto.NegotiationHistoryDto;
import com.smartpricing.entity.Negotiation;
import com.smartpricing.entity.User;
import com.smartpricing.repository.NegotiationRepository;
import com.smartpricing.repository.UserRepository;
import com.smartpricing.util.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/negotiation/history")
public class NegotiationHistoryController {

    private final NegotiationRepository negotiationRepository;
    private final UserRepository userRepository;

    public NegotiationHistoryController(
            NegotiationRepository negotiationRepository,
            UserRepository userRepository
    ) {
        this.negotiationRepository = negotiationRepository;
        this.userRepository = userRepository;
    }

    // =========================
    // 🔐 USER HISTORY
    // =========================
    @Operation(
    	    summary = "Negotiation history",
    	    description = "Fetch logged-in user's negotiation history"
    	)
    @GetMapping
    public ApiResponse<List<NegotiationHistoryDto>> getMyNegotiations() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Negotiation> negotiations =
                negotiationRepository.findByUserId(user.getUserId());

        return new ApiResponse<>(
                200,
                "Negotiation history fetched",
                mapToDto(negotiations)
        );
    }

    // =========================
    // 🔐 PRODUCT HISTORY
    // =========================
    @GetMapping("/product/{productId}")
    public ApiResponse<List<NegotiationHistoryDto>> getProductNegotiations(
            @PathVariable Long productId
    ) {
        List<Negotiation> negotiations =
                negotiationRepository.findByProductId(productId);

        return new ApiResponse<>(
                200,
                "Product negotiation history fetched",
                mapToDto(negotiations)
        );
    }

    // =========================
    // 🔄 Mapper
    // =========================
    private List<NegotiationHistoryDto> mapToDto(List<Negotiation> negotiations) {

        return negotiations.stream().map(n -> {
            NegotiationHistoryDto dto = new NegotiationHistoryDto();
            dto.setNegotiationId(n.getNegotiationId());
            dto.setProductId(n.getProductId());
            dto.setOfferedPrice(n.getOfferedPrice());
            dto.setFinalPrice(n.getFinalPrice());
            dto.setStatus(n.getStatus().name());
            dto.setAttemptNumber(n.getAttemptNumber());
            dto.setCreatedAt(n.getCreatedAt());
            return dto;
        }).collect(Collectors.toList());
    }
}
