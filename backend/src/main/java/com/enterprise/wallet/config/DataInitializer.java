package com.enterprise.wallet.config;

import com.enterprise.wallet.entity.User;
import com.enterprise.wallet.entity.Wallet;
import com.enterprise.wallet.repository.UserRepository;
import com.enterprise.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Random;

/**
 * Data Initializer - Creates demo accounts on application startup
 * This runs only once when the database is empty
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initializeData() {
        return args -> {
            // Create demo superadmin account if it doesn't exist
            if (!userRepository.existsByUsername("superadmin")) {
                createDemoSuperAdmin();
            }

            // Create demo admin account if it doesn't exist
            if (!userRepository.existsByUsername("admin")) {
                createDemoAdmin();
            }

            // Create demo regular user if it doesn't exist
            if (!userRepository.existsByUsername("demouser")) {
                createDemoUser();
            }

            log.info("========================================");
            log.info("DEMO ACCOUNTS INITIALIZED");
            log.info("========================================");
            log.info("SUPERADMIN Account:");
            log.info("  Username: superadmin");
            log.info("  Email: superadmin@wallet.com");
            log.info("  Password: SuperAdmin@123");
            log.info("  Role: SUPERUSER");
            log.info("========================================");
            log.info("ADMIN Account:");
            log.info("  Username: admin");
            log.info("  Email: admin@wallet.com");
            log.info("  Password: Admin@123");
            log.info("  Role: ADMIN");
            log.info("========================================");
            log.info("DEMO USER Account:");
            log.info("  Username: demouser");
            log.info("  Email: demo@wallet.com");
            log.info("  Password: Demo@123");
            log.info("  Role: USER");
            log.info("========================================");
        };
    }

    @Transactional
    private void createDemoSuperAdmin() {
        User superAdmin = new User();
        superAdmin.setUsername("superadmin");
        superAdmin.setEmail("superadmin@wallet.com");
        superAdmin.setPassword(passwordEncoder.encode("SuperAdmin@123"));
        superAdmin.setFullName("Super Administrator");
        superAdmin.setPhoneNumber("+92-300-1234567");
        superAdmin.setCnicNumber("12345-6789012-3");
        superAdmin.setKycStatus(User.KycStatus.VERIFIED);
        superAdmin.setUserRole(User.UserRole.SUPERUSER);
        superAdmin.setIsActive(true);

        User savedUser = userRepository.save(superAdmin);
        createWalletForUser(savedUser, new BigDecimal("100000")); // Initial balance for demo

        log.info("Created demo SUPERADMIN account");
    }

    @Transactional
    private void createDemoAdmin() {
        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@wallet.com");
        admin.setPassword(passwordEncoder.encode("Admin@123"));
        admin.setFullName("System Administrator");
        admin.setPhoneNumber("+92-300-7654321");
        admin.setCnicNumber("12345-6789012-4");
        admin.setKycStatus(User.KycStatus.VERIFIED);
        admin.setUserRole(User.UserRole.ADMIN);
        admin.setIsActive(true);

        User savedUser = userRepository.save(admin);
        createWalletForUser(savedUser, new BigDecimal("50000")); // Initial balance for demo

        log.info("Created demo ADMIN account");
    }

    @Transactional
    private void createDemoUser() {
        User user = new User();
        user.setUsername("demouser");
        user.setEmail("demo@wallet.com");
        user.setPassword(passwordEncoder.encode("Demo@123"));
        user.setFullName("Demo User");
        user.setPhoneNumber("+92-300-9876543");
        user.setCnicNumber("12345-6789012-5");
        user.setKycStatus(User.KycStatus.VERIFIED);
        user.setUserRole(User.UserRole.USER);
        user.setIsActive(true);

        User savedUser = userRepository.save(user);
        createWalletForUser(savedUser, new BigDecimal("10000")); // Initial balance for demo

        log.info("Created demo USER account");
    }

    private void createWalletForUser(User user, BigDecimal initialBalance) {
        Wallet wallet = new Wallet();
        wallet.setWalletNumber(generateWalletNumber());
        wallet.setUser(user);
        wallet.setBalance(initialBalance);
        wallet.setCurrency("PKR");
        wallet.setWalletStatus(Wallet.WalletStatus.ACTIVE);
        wallet.setWalletType(Wallet.WalletType.PERSONAL);

        walletRepository.save(wallet);
    }

    private String generateWalletNumber() {
        String prefix = "WLT";
        Random random = new Random();
        String number;

        do {
            number = prefix + String.format("%010d", random.nextInt(1000000000));
        } while (walletRepository.existsByWalletNumber(number));

        return number;
    }
}
