package com.rainbowwash.service;

import com.rainbowwash.dto.CreateEmployeeRequest;
import com.rainbowwash.dto.EmployeeSummary;
import com.rainbowwash.model.User;
import com.rainbowwash.model.UserRole;
import com.rainbowwash.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom random = new SecureRandom();

    // Word lists kept short, common, and easy to say/spell over the phone or WhatsApp.
    // Combined with a 4-digit number the space is still large enough (60*60*10000 =
    // 36,000,000 combinations) to be a safe one-time temp password, while being far
    // easier for a Staff/Manager to read and retype correctly than a random string
    // like "K1JIzhI0" (which mixes visually similar characters: I/l/1, 0/O).
    private static final String[] WORDS = {
            "Coral", "Otter", "Maple", "Amber", "Pixel", "Comet", "Delta", "Ember",
            "Falcon", "Garnet", "Harbor", "Indigo", "Jasper", "Kettle", "Lagoon",
            "Meadow", "Nectar", "Orchid", "Petal", "Quartz", "River", "Summit",
            "Tundra", "Umbra", "Violet", "Willow", "Zephyr", "Cocoa", "Denim",
            "Ember", "Frost", "Grove", "Haven", "Ivory", "Juniper", "Koala",
            "Lantern", "Marble", "Nomad", "Onyx", "Prairie", "Quill", "Ripple",
            "Sable", "Thistle", "Umber", "Vapor", "Walnut", "Yonder", "Zenith",
            "Breeze", "Canyon", "Dune", "Ebony", "Fern", "Glacier", "Hazel",
            "Iris", "Juniper", "Kite", "Lotus"
    };

    public EmployeeService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Returns the plaintext temporary password so the caller (AdminController) can
    // hand it back to the Admin who provisioned the account, once, at creation time.
    // It is never stored or logged in plaintext anywhere after this call returns.
    public String provisionEmployee(CreateEmployeeRequest createEmployeeRequest) {
        if (userRepository.existsByEmail(createEmployeeRequest.getEmail())) {
            throw new RuntimeException("An account with this email already exist.");
        }

        String tempPassword = generateTemporaryPassword();

        User newUser = new User();
        newUser.setFullName(createEmployeeRequest.getFullName());
        newUser.setEmail(createEmployeeRequest.getEmail());
        newUser.setPassword(passwordEncoder.encode(tempPassword));
        newUser.setRole(createEmployeeRequest.getRole());
        newUser.setFirstLogin(true);

        userRepository.save(newUser);

        return tempPassword;
    }

    // Lists Staff and Manager accounts for the Admin dashboard. Admin accounts are
    // excluded here deliberately — this list is for managing employees, not for
    // browsing other admins. Never includes password/hash data.
    public List<EmployeeSummary> listEmployees() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() != UserRole.ADMIN)
                .map(u -> new EmployeeSummary(u.getId(), u.getFullName(), u.getEmail(), u.getRole().name()))
                .collect(Collectors.toList());
    }

    // Generates a brand new temporary password for an existing user and overwrites
    // their stored hash. This is the safe substitute for "showing" a forgotten
    // password: the old one is never recoverable, but the Admin can always issue a
    // fresh one and relay it to the staff member.
    public String resetPassword(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String tempPassword = generateTemporaryPassword();
        user.setPassword(passwordEncoder.encode(tempPassword));
        user.setFirstLogin(true);
        userRepository.save(user);

        return tempPassword;
    }

    // Produces something like "Coral-Otter-4821": two random dictionary words plus
    // a 4-digit number. Memorable and easy to retype correctly, while still random
    // enough to be safe as a one-time temp password (the user is always forced to
    // change it on first login via firstLogin=true).
    private String generateTemporaryPassword() {
        String word1 = WORDS[random.nextInt(WORDS.length)];
        String word2 = WORDS[random.nextInt(WORDS.length)];
        int number = 1000 + random.nextInt(9000); // 4-digit, 1000-9999

        return word1 + "-" + word2 + "-" + number;
    }
}