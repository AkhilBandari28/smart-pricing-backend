package com.smartpricing.service;

import com.smartpricing.dto.LoginRequestDto;
import com.smartpricing.dto.LoginResponseDto;
import com.smartpricing.dto.RegisterRequestDto;
import com.smartpricing.entity.Role;
import com.smartpricing.entity.User;
import com.smartpricing.exception.BusinessException;
import com.smartpricing.repository.UserRepository;
import com.smartpricing.util.JwtUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // =========================
    // REGISTER
    // =========================
    public void register(RegisterRequestDto request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BusinessException("Email already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // ✅ DEFAULT ROLE = USER
        user.setRole(Role.USER);
        user.setTrustScore(0.0);

        userRepository.save(user);
    }

    // =========================
    // LOGIN
    // =========================
    public LoginResponseDto login(LoginRequestDto request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

        return new LoginResponseDto(
                token,
                user.getRole().name()
        );
    }

    // =========================
    // PROFILE (NEW)
    // =========================
    public LoginResponseDto getProfile(Authentication authentication) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new BusinessException("User not found"));

        return new LoginResponseDto(
                null,                     // token not needed again
                user.getRole().name()
        );
    }
}
