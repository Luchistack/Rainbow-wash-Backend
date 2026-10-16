package com.rainbowwash;

import com.rainbowwash.config.LocalAdminInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.rainbowwash")
@Import(LocalAdminInitializer.class)
public class LocalTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(LocalTestApplication.class, args);
    }
}     
