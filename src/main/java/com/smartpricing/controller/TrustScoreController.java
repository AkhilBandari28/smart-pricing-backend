package com.smartpricing.controller;

import com.smartpricing.dto.TrustScoreHistoryDto;
import com.smartpricing.service.TrustScoreService;
import com.smartpricing.util.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trust-score")
public class TrustScoreController {

    private final TrustScoreService trustScoreService;

    public TrustScoreController(TrustScoreService trustScoreService) {
        this.trustScoreService = trustScoreService;
    }

    // =====================================================
    // 👤 USER – View own trust score history
    // =====================================================
    @GetMapping("/history")
    public ApiResponse<List<TrustScoreHistoryDto>> myHistory() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        // User ID resolved internally (clean separation)
        Long userId = trustScoreService.getUserIdByEmail(email);

        return new ApiResponse<>(
                200,
                "Your trust score history",
                trustScoreService.getUserHistory(userId)
        );
    }

    // =====================================================
    // 🛡 ADMIN – View any user's trust history
    // =====================================================
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/history/{userId}")
    public ApiResponse<List<TrustScoreHistoryDto>> historyByUser(
            @PathVariable Long userId
    ) {
        return new ApiResponse<>(
                200,
                "User trust score history",
                trustScoreService.getHistoryByUser(userId)
        );
    }
}
