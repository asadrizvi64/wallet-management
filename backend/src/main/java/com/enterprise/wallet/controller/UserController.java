package com.enterprise.wallet.controller;

import com.enterprise.wallet.dto.UserDTOs.*;
import com.enterprise.wallet.dto.OtherDTOs.*;
import com.enterprise.wallet.entity.User;
import com.enterprise.wallet.entity.Wallet;
import com.enterprise.wallet.service.UserService;
import com.enterprise.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * User Controller - REST APIs for user management
 * Base URL: /api/v1/users
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {
    
    private final UserService userService;
    private final WalletService walletService;
    
    /**
     * API 1: POST /api/v1/users/register
     * Register new user
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserProfileResponse>> registerUser(
            @RequestBody RegisterRequest request) {
        
        try {
            User user = userService.registerUser(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getFullName(),
                request.getPhoneNumber(),
                request.getCnicNumber()
            );
            
            Wallet wallet = walletService.getWalletByUserId(user.getId());

            UserProfileResponse response = new UserProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getPhoneNumber(),
                user.getCnicNumber(),
                user.getKycStatus().toString(),
                wallet.getWalletNumber(),
                wallet.getBalance()
            );

            return ResponseEntity.ok(
                ApiResponse.success("User registered successfully", response)
            );
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                ApiResponse.error(e.getMessage())
            );
        }
    }
    
    /**
     * API 2: POST /api/v1/users/login
     * User authentication
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> loginUser(
            @RequestBody LoginRequest request) {
        
        try {
            User user = userService.authenticateUser(
                request.getEmail(),
                request.getPassword()
            );
            
            Wallet wallet = walletService.getWalletByUserId(user.getId());

            // In real app, generate JWT token here
            String token = "TOKEN-" + System.currentTimeMillis();

            LoginResponse response = new LoginResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                token,
                wallet.getWalletNumber()
            );

            return ResponseEntity.ok(
                ApiResponse.success("Login successful", response)
            );
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                ApiResponse.error(e.getMessage())
            );
        }
    }
    
    /**
     * API 3: PUT /api/v1/users/{userId}
     * Update user profile
     */
    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            @PathVariable Long userId,
            @RequestBody UpdateProfileRequest request) {
        
        try {
            User user = userService.updateUserProfile(
                userId,
                request.getFullName(),
                request.getPhoneNumber()
            );
            
            Wallet wallet = walletService.getWalletByUserId(user.getId());

            UserProfileResponse response = new UserProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getPhoneNumber(),
                user.getCnicNumber(),
                user.getKycStatus().toString(),
                wallet.getWalletNumber(),
                wallet.getBalance()
            );

            return ResponseEntity.ok(
                ApiResponse.success("Profile updated successfully", response)
            );
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                ApiResponse.error(e.getMessage())
            );
        }
    }
    
    /**
     * API 4: GET /api/v1/users/{userId}
     * Get user details
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getUserDetails(
            @PathVariable Long userId) {
        
        try {
            User user = userService.getUserById(userId);
            Wallet wallet = walletService.getWalletByUserId(user.getId());

            UserProfileResponse response = new UserProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getPhoneNumber(),
                user.getCnicNumber(),
                user.getKycStatus().toString(),
                wallet.getWalletNumber(),
                wallet.getBalance()
            );

            return ResponseEntity.ok(
                ApiResponse.success("User details retrieved", response)
            );
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                ApiResponse.error(e.getMessage())
            );
        }
    }
    
    /**
     * API 5: POST /api/v1/users/{userId}/kyc
     * Verify KYC (Admin endpoint)
     */
    @PostMapping("/{userId}/kyc")
    public ResponseEntity<ApiResponse<String>> verifyKyc(
            @PathVariable Long userId,
            @RequestParam boolean approved) {
        
        try {
            User user = userService.verifyKyc(userId, approved);
            
            String message = approved ? 
                "KYC verified successfully" : 
                "KYC rejected";
            
            return ResponseEntity.ok(
                ApiResponse.success(user.getKycStatus().toString(), message)
            );
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                ApiResponse.error(e.getMessage())
            );
        }
    }
}

/**
 * Additional DTO for update profile
 */
class UpdateProfileRequest {
    private String fullName;
    private String phoneNumber;
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}
