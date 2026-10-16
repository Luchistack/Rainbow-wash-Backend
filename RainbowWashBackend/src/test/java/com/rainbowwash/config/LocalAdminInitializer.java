package com.rainbowwash.config;

import com.rainbowwash.model.User;
import com.rainbowwash.model.UserRole;
import com.rainbowwash.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("test")
public class LocalAdminInitializer {

    @Bean
    CommandLineRunner createLocalAdmin(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {
            String email = "admin.test@rainbowwash.com";

            if (!userRepository.existsByEmail(email)) {
                User admin = new User();
                admin.setFullName("Local Test Admin");
                admin.setEmail(email);
                admin.setPassword(
                    passwordEncoder.encode("RainbowAdmin@2026!")
                );
                admin.setRole(UserRole.ADMIN);
                admin.setFirstLogin(false);

                userRepository.save(admin);
                System.out.println(
                    "LOCAL TEST ADMIN CREATED: " + email
                );
            }
        };
    }
}
