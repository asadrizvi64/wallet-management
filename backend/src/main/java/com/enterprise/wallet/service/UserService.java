package com.enterprise.wallet.service;

import com.enterprise.wallet.entity.User;
import com.enterprise.wallet.entity.Wallet;
import com.enterprise.wallet.repository.UserRepository;
import com.enterprise.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Random;

/**
 * User Service - Business logic for user operations
 */
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * API 1: Register new user
     */
    @Transactional
    public User registerUser(String username, String email, String password,
                            String fullName, String phoneNumber, String cnicNumber) {

        // Validate unique constraints
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already registered");
        }
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already taken");
        }
        if (phoneNumber != null && userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new RuntimeException("Phone number already registered");
        }

        // Create user
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setFullName(fullName);
        user.setPhoneNumber(phoneNumber);
        user.setCnicNumber(cnicNumber);
        user.setKycStatus(User.KycStatus.PENDING);
        user.setUserRole(User.UserRole.USER);
        user.setIsActive(true);

        User savedUser = userRepository.save(user);

        // Auto-create wallet for user
        createWalletForUser(savedUser);

        return savedUser;
    }
    
    /**
     * API 2: User login/authentication
     * Accepts either email or username
     */
    public User authenticateUser(String emailOrUsername, String password) {
        // Try to find user by email first, then by username
        User user = userRepository.findByEmail(emailOrUsername)
            .orElse(userRepository.findByUsername(emailOrUsername)
                .orElseThrow(() -> new RuntimeException("User not found")));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        if (!user.getIsActive()) {
            throw new RuntimeException("Account is deactivated");
        }

        return user;
    }
    
    /**
     * API 3: Update user profile
     */
    @Transactional
    public User updateUserProfile(Long userId, String fullName, String phoneNumber) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (fullName != null) user.setFullName(fullName);
        if (phoneNumber != null) {
            if (userRepository.existsByPhoneNumber(phoneNumber)) {
                throw new RuntimeException("Phone number already in use");
            }
            user.setPhoneNumber(phoneNumber);
        }
        
        return userRepository.save(user);
    }
    
    /**
     * API 4: Get user details
     */
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    /**
     * API 5: KYC verification (Admin function)
     */
    @Transactional
    public User verifyKyc(Long userId, boolean approved) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setKycStatus(approved ? User.KycStatus.VERIFIED : User.KycStatus.REJECTED);
        
        return userRepository.save(user);
    }
    
    /**
     * Helper: Create wallet for new user
     */
    private void createWalletForUser(User user) {
        Wallet wallet = new Wallet();
        wallet.setWalletNumber(generateWalletNumber());
        wallet.setUser(user);
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setCurrency("PKR");
        wallet.setWalletStatus(Wallet.WalletStatus.ACTIVE);
        wallet.setWalletType(Wallet.WalletType.PERSONAL);

        walletRepository.save(wallet);
    }
    
    /**
     * Generate unique wallet number
     */
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
