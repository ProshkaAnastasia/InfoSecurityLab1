package com.security.api.config;

import com.security.api.entity.User;
import com.security.api.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    @Profile("dev")
    public CommandLineRunner initDevData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                logger.info("Initializing test data (DEV profile only)");

                // Test user
                User testUser = new User();
                testUser.setUsername("user");
                testUser.setPassword(passwordEncoder.encode("password123"));
                testUser.setEmail("user@example.com");
                testUser.setRoles(Set.of("ROLE_USER"));
                userRepository.save(testUser);
                logger.info("Created test user: user/password123");

                // Test admin
                User testAdmin = new User();
                testAdmin.setUsername("admin");
                testAdmin.setPassword(passwordEncoder.encode("admin123"));
                testAdmin.setEmail("admin@example.com");
                testAdmin.setRoles(Set.of("ROLE_USER", "ROLE_ADMIN"));
                userRepository.save(testAdmin);
                logger.info("Created test admin: admin/admin123");
            }
        };
    }

    @Bean
    @Profile("prod")
    public CommandLineRunner initProdData(UserRepository userRepository) {
        return args -> {
            logger.info("Production profile - skipping test data initialization");
        };
    }
}