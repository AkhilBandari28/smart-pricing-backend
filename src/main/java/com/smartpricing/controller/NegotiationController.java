//package com.smartpricing.controller;
//
//import com.smartpricing.dto.NegotiationRequestDto;
//import com.smartpricing.dto.NegotiationResponseDto;
//import com.smartpricing.service.NegotiationService;
//import com.smartpricing.util.ApiResponse;
//
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import jakarta.validation.Valid;
//
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.bind.annotation.*;
//
//
//@Tag(
//	    name = "Negotiation APIs",
//	    description = "AI-based price negotiation using trust score & smart rules"
//	)
//@RestController
//@RequestMapping("/api/negotiation")
//public class NegotiationController {
//
//    private final NegotiationService negotiationService;
//
//    public NegotiationController(NegotiationService negotiationService) {
//        this.negotiationService = negotiationService;
//    }
//
//    @Operation(
//            summary = "Negotiate product price",
//            description = "Allows a user to negotiate product price. Price is accepted, rejected, or countered based on trust score."
//        )
//    @PostMapping
//    public ApiResponse<NegotiationResponseDto> negotiate(
//    		@Valid @RequestBody NegotiationRequestDto request
//    ) {
//        String email = SecurityContextHolder.getContext()
//                .getAuthentication()
//                .getName();
//
//        return new ApiResponse<>(
//                200,
//                "Negotiation processed",
//                negotiationService.negotiate(request, email)
//        );
//    }
//}


// AI FEATURES-----------------------------------------


package com.smartpricing.controller;

import com.smartpricing.dto.NegotiationRequestDto;
import com.smartpricing.dto.NegotiationResponseDto;
import com.smartpricing.service.NegotiationService;
import com.smartpricing.util.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Tag(
    name = "Negotiation APIs",
    description = "Smart price negotiation (Rule-based & AI-ready)"
)
@RestController
@RequestMapping("/api/negotiation")
public class NegotiationController {

    private final NegotiationService negotiationService;

    public NegotiationController(NegotiationService negotiationService) {
        this.negotiationService = negotiationService;
    }

    @Operation(
        summary = "Negotiate product price",
        description = """
            Allows a user to negotiate product price.
            
            Decision mode:
            - RULE : Uses trust-score rule engine (default)
            - AI   : Uses ML / Gen-AI decision engine (future)
        """
    )
    @PostMapping
    public ApiResponse<NegotiationResponseDto> negotiate(
            @Valid @RequestBody NegotiationRequestDto request,

            // 🔥 AI SWITCH (OPTIONAL)
            @RequestParam(
                defaultValue = "RULE",
                required = false
            ) String decisionMode
    ) {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        NegotiationResponseDto response =
                negotiationService.negotiate(
                        request,
                        email,
                        decisionMode
                );

        return new ApiResponse<>(
                200,
                "Negotiation processed using " + decisionMode + " mode",
                response
        );
    }
}

