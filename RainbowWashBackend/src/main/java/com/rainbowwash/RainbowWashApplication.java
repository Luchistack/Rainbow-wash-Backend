package com.rainbowwash;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

// @EnableScheduling powers the rate limiter's periodic memory cleanup
// (RateLimitFilter.cleanupStaleEntries) — without it, that @Scheduled method
// would simply never run.
@EnableScheduling
@SpringBootApplication
public class RainbowWashApplication {
    public static void main(String[] args) {
        SpringApplication.run(RainbowWashApplication.class, args);
    }
}