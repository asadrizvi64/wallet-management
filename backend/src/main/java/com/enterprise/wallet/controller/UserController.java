package com.enterprise.wallet.controller;

import com.enterprise.wallet.dto.UserDTOs.*;
import com.enterprise.wallet.dto.OtherDTOs.*;
import com.enterprise.wallet.entity.User;
import com.enterprise.wallet.entity.Wallet;
import com.enterprise.wallet.security.JwtTokenProvider;
import com.enterprise.wallet.security.UserPrincipal;
import com.enterprise.wallet.service.UserService;
import com.enterprise.wallet.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * User Controller - REST APIs for user management
 * Base URL: /api/v1/users
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class UserController {

    private final UserService userService;
    private final WalletService walletService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    
    /**
     * API 1: POST /api/v1/users/register
     * Register new user
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserProfileResponse>> registerUser(
            @Valid @RequestBody RegisterRequest request) {
        
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
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getPhoneNumber(),
                user.getCnicNumber(),
                user.getKycStatus().toString(),
                user.getUserRole().toString(),
                user.getIsActive(),
                wallet.getWalletNumber(),
                wallet.getBalance(),
                user.getCreatedAt(),
                user.getUpdatedAt()
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
            @Valid @RequestBody LoginRequest request) {

        log.debug("Login attempt for user: {}", request.getEmail());

        try {
            // Authenticate user
            log.debug("Authenticating user with email/username: {}", request.getEmail());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            log.debug("Authentication successful for user: {}", request.getEmail());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate JWT token
            String token = tokenProvider.generateToken(authentication);

            // Get user details from authenticated principal
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            User user = userService.getUserById(userPrincipal.getId());
            Wallet wallet = walletService.getWalletByUserId(user.getId());

            log.info("User logged in successfully: {}", user.getEmail());

            LoginResponse response = new LoginResponse(
                    user.getId(),
                    user.getEmail(),
                    user.getFullName(),
                    user.getUserRole().toString(),
                    token,
                    wallet.getWalletNumber()
            );

            return ResponseEntity.ok(
                    ApiResponse.success("Login successful", response)
            );

        } catch (Exception e) {
            log.error("Login failed for user: {}. Error: {}", request.getEmail(), e.getMessage());
            log.debug("DEBUG: Login failed for {}", request.getEmail());
            log.debug("DEBUG: Exception type: {}", e.getClass().getName());
            log.debug("DEBUG: Exception message: {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("Invalid email or password")
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
            @Valid @RequestBody UpdateProfileRequest request) {
        
        try {
            User user = userService.updateUserProfile(
                userId,
                request.getFullName(),
                request.getPhoneNumber()
            );
            
            Wallet wallet = walletService.getWalletByUserId(user.getId());

            UserProfileResponse response = new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getPhoneNumber(),
                user.getCnicNumber(),
                user.getKycStatus().toString(),
                user.getUserRole().toString(),
                user.getIsActive(),
                wallet.getWalletNumber(),
                wallet.getBalance(),
                user.getCreatedAt(),
                user.getUpdatedAt()
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
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getPhoneNumber(),
                user.getCnicNumber(),
                user.getKycStatus().toString(),
                user.getUserRole().toString(),
                user.getIsActive(),
                wallet.getWalletNumber(),
                wallet.getBalance(),
                user.getCreatedAt(),
                user.getUpdatedAt()
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

    /**
     * API 6: GET /api/v1/users
     * Get all users (Admin endpoint)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminUserResponse>>> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();

            List<AdminUserResponse> response = users.stream()
                .map(user -> {
                    Wallet wallet = walletService.getWalletByUserId(user.getId());
                    return new AdminUserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getFullName(),
                        user.getPhoneNumber(),
                        user.getCnicNumber(),
                        user.getKycStatus().toString(),
                        user.getUserRole().toString(),
                        user.getIsActive(),
                        wallet.getWalletNumber(),
                        wallet.getBalance(),
                        user.getCreatedAt(),
                        user.getUpdatedAt()
                    );
                })
                .collect(Collectors.toList());

            return ResponseEntity.ok(
                ApiResponse.success("Users retrieved successfully", response)
            );

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                ApiResponse.error(e.getMessage())
            );
        }
    }

    /**
     * API 7: PUT /api/v1/users/{userId}/admin
     * Update user admin fields (Admin endpoint)
     */
    @PutMapping("/{userId}/admin")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateUserAdminFields(
            @PathVariable Long userId,
            @Valid @RequestBody AdminUpdateRequest request) {

        try {
            User user = userService.updateUserAdminFields(
                userId,
                request.getUserRole() != null ? User.UserRole.valueOf(request.getUserRole()) : null,
                request.getKycStatus() != null ? User.KycStatus.valueOf(request.getKycStatus()) : null,
                request.getIsActive()
            );

            Wallet wallet = walletService.getWalletByUserId(user.getId());

            UserProfileResponse response = new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getPhoneNumber(),
                user.getCnicNumber(),
                user.getKycStatus().toString(),
                user.getUserRole().toString(),
                user.getIsActive(),
                wallet.getWalletNumber(),
                wallet.getBalance(),
                user.getCreatedAt(),
                user.getUpdatedAt()
            );

            return ResponseEntity.ok(
                ApiResponse.success("User updated successfully", response)
            );

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                ApiResponse.error(e.getMessage())
            );
        }
    }
}

/**
 * Additional DTOs
 */
class UpdateProfileRequest {
    private String fullName;
    private String phoneNumber;

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}

class AdminUpdateRequest {
    private String kycStatus;
    private String userRole;
    private Boolean isActive;

    public String getKycStatus() { return kycStatus; }
    public void setKycStatus(String kycStatus) { this.kycStatus = kycStatus; }
    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
