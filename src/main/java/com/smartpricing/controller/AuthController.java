package com.smartpricing.controller;

import com.smartpricing.dto.LoginRequestDto;
import com.smartpricing.dto.LoginResponseDto;
import com.smartpricing.dto.RegisterRequestDto;
import com.smartpricing.service.AuthService;
import com.smartpricing.util.ApiResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(
    name = "Authentication APIs",
    description = "User registration, login and authentication related APIs"
)
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // =========================
    // ✅ REGISTER
    // =========================
    @Operation(
        summary = "User registration",
        description = "Registers a new user with role and initial trust score"
    )
    @PostMapping("/register")
    public ApiResponse<String> register(
            @Valid @RequestBody RegisterRequestDto request) {

        authService.register(request);

        return new ApiResponse<>(
                201,
                "User registered successfully",
                "User registered successfully"
        );
    }

    // =========================
    // ✅ LOGIN
    // =========================
    @Operation(
        summary = "User login",
        description = "Authenticates user credentials and returns a JWT token"
    )
    @PostMapping("/login")
    public ApiResponse<LoginResponseDto> login(
            @Valid @RequestBody LoginRequestDto request) {

        LoginResponseDto response = authService.login(request);

        return new ApiResponse<>(
                200,
                "Login successful",
                response
        );
    }
    
    @Operation(
    	    summary = "Get logged-in user profile",
    	    description = "Returns basic details of the currently authenticated user"
    	)
    	@GetMapping("/profile")
    	public ApiResponse<LoginResponseDto> profile() {

    	    Authentication auth =
    	            SecurityContextHolder.getContext().getAuthentication();

    	    LoginResponseDto profile = authService.getProfile(auth);

    	    return new ApiResponse<>(
    	            200,
    	            "User profile fetched",
    	            profile
    	    );
    	}
}
