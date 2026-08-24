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
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Only /login is public — change-password (and any future /api/auth/**
                        // route) now correctly requires a valid JWT via anyRequest().authenticated().
                        .requestMatchers("/api/auth/login").permitAll()

                        // Customers browsing the public site need to read services and products
                        // without logging in — but creating/editing/deleting them (POST/PUT/DELETE)
                        // now correctly falls through to anyRequest().authenticated(), which was NOT
                        // true before (the old rule permitted all methods, not just GET).
                        .requestMatchers(HttpMethod.GET, "/api/services", "/api/services/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/products").permitAll()

                        // Customers submitting a booking or a laundry order aren't logged in either.
                        .requestMatchers(HttpMethod.POST, "/api/bookings").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/orders").permitAll()

                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                );

        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}