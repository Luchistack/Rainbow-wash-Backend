package com.rainbowwash;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BcryptHashGenerator {
    @Test
    void printHash() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String plainPassword = "12345678";
        String hash = encoder.encode(plainPassword);
        System.out.println("BCRYPT_HASH_START");
        System.out.println(hash);
        System.out.println("BCRYPT_HASH_END");
    }
}
