package com.rainbowwash.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(JwtTokenProvider tokenProvider, UserDetailsService userDetailsService) {
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(tokenProvider, userDetailsService);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults()) // Ensure CORS is enabled
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Allow login for both with and without /api prefix
                        .requestMatchers("/auth/**", "/api/auth/**").permitAll()

                        // Allow public reading of services and products
                        .requestMatchers(HttpMethod.GET, "/services", "/api/services", "/services/**", "/api/services/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/products", "/api/products").permitAll()

                        // Allow public bookings and orders
                        .requestMatchers(HttpMethod.POST, "/bookings", "/api/bookings").permitAll()
                        .requestMatchers(HttpMethod.POST, "/orders", "/api/orders").permitAll()

                        .requestMatchers("/admin/**", "/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                );

        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}