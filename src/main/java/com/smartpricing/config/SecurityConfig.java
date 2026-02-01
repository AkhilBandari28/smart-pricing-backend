package com.smartpricing.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // ✅ ENABLE CORS (Frontend support)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // ❌ Disable CSRF (REST APIs)
            .csrf(csrf -> csrf.disable())

            // 🔐 Stateless JWT session
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .authorizeHttpRequests(auth -> auth

                // =========================
                // 🔓 AUTH + SWAGGER
                // =========================
                .requestMatchers(
                        "/api/auth/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**"
                ).permitAll()

                // =========================
                // 🔓 PUBLIC PRODUCTS
                // =========================
                .requestMatchers(HttpMethod.GET, "/api/products/**")
                .permitAll()

                // =========================
                // 🔐 ADMIN PRODUCT MANAGEMENT
                // =========================
                .requestMatchers(HttpMethod.POST, "/api/products/**")
                .hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/products/**")
                .hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/products/**")
                .hasAuthority("ADMIN")

                // =========================
                // 📊 ADMIN DASHBOARD 🔥 ADDED
                // =========================
                .requestMatchers("/api/admin/**")
                .hasAuthority("ADMIN")

                // =========================
                // 🤝 NEGOTIATION (USER + ADMIN)
                // =========================
                .requestMatchers(HttpMethod.POST, "/api/negotiation/**")
                .hasAnyAuthority("USER", "ADMIN")

                // =========================
                // 🛒 CHECKOUT
                // =========================
                .requestMatchers(HttpMethod.POST, "/api/checkout/**")
                .authenticated()

                // =========================
                // 📦 ORDERS
                // =========================
                .requestMatchers("/api/orders/**")
                .authenticated()
                .requestMatchers("/api/orders/history/admin")
                .hasAuthority("ADMIN")
                .requestMatchers("/api/orders/history/**")
                .authenticated()

                // =========================
                // 🔒 EVERYTHING ELSE
                // =========================
                .anyRequest().authenticated()
            )

            // 🔥 JWT FILTER
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ✅ CORS CONFIG
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);

        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
