package com.rainbowwash.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

// Simple in-memory sliding-window rate limiter, keyed by client IP + path.
// No external dependency (no Bucket4j, no Redis) — appropriate for a single
// Railway instance. If this ever moves to multiple replicas, the counters
// would need to move to a shared store (Redis) since each instance would
// otherwise track its own separate counts, effectively multiplying the
// real limit by however many replicas are running.
//
// Only the specific public endpoints that accept anonymous traffic are
// limited here — every authenticated staff action, and every GET request,
// passes straight through untouched.
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    // path -> { maxRequests, windowMillis }
    private static final Map<String, int[]> LIMITS = Map.of(
            "/api/auth/login", new int[]{5, 5 * 60 * 1000},   // 5 attempts per 5 minutes — brute-force protection
            "/api/orders", new int[]{10, 60 * 1000},           // 10 laundry orders per minute per IP
            "/api/bookings", new int[]{10, 60 * 1000},         // 10 cleaning bookings per minute per IP
            "/api/shop-orders", new int[]{10, 60 * 1000}       // 10 shop orders per minute per IP
    );

    private final Map<String, Deque<Long>> requestLog = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        int[] limit = "POST".equals(request.getMethod()) ? LIMITS.get(path) : null;

        if (limit == null) {
            filterChain.doFilter(request, response);
            return;
        }

        int maxRequests = limit[0];
        long windowMs = limit[1];
        String key = getClientIp(request) + ":" + path;
        long now = System.currentTimeMillis();

        Deque<Long> timestamps = requestLog.computeIfAbsent(key, k -> new ConcurrentLinkedDeque<>());

        synchronized (timestamps) {
            while (!timestamps.isEmpty() && now - timestamps.peekFirst() > windowMs) {
                timestamps.pollFirst();
            }

            if (timestamps.size() >= maxRequests) {
                response.setStatus(429); // Too Many Requests
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Too many requests, please try again in a moment.\"}");
                return;
            }

            timestamps.addLast(now);
        }

        filterChain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        // Railway sits behind a proxy, so the real client IP arrives in this
        // header rather than as the direct remote address.
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    // Without this, an IP that hits the site once and never returns leaves an
    // empty (but not-yet-garbage-collected) entry sitting in memory forever.
    // Sweeping every 10 minutes keeps long-term memory use flat regardless of
    // how many distinct visitors the site sees over time.
    @Scheduled(fixedRate = 10 * 60 * 1000)
    public void cleanupStaleEntries() {
        long now = System.currentTimeMillis();
        requestLog.forEach((key, timestamps) -> {
            synchronized (timestamps) {
                while (!timestamps.isEmpty() && now - timestamps.peekFirst() > 10 * 60 * 1000) {
                    timestamps.pollFirst();
                }
            }
        });
        requestLog.values().removeIf(Deque::isEmpty);
    }
}