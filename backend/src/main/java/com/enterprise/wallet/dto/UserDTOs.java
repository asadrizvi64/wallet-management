package com.enterprise.wallet.dto;

import com.enterprise.wallet.entity.User;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * User-related Data Transfer Objects
 */
public class UserDTOs {

    // ========== Auth DTOs ==========

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {
        @NotBlank(message = "Username is required")
        private String username;

        @NotBlank(message = "Password is required")
        private String password;

        // Additional fields for compatibility
        private String email;

        public String getEmail() {
            return email != null ? email : username;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterRequest {
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50)
        private String username;

        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        private String email;

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        private String password;

        @NotBlank(message = "Full name is required")
        private String fullName;

        private String phoneNumber;

        private String cnicNumber;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthResponse {
        private String token;
        private String type = "Bearer";
        private Long userId;
        private String username;
        private String email;
        private String fullName;
        private User.UserRole role;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginResponse {
        private Long userId;
        private String email;
        private String fullName;
        private String token;
        private String walletNumber;
    }

    // ========== User DTOs ==========

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserResponse {
        private Long id;
        private String username;
        private String email;
        private String fullName;
        private String phoneNumber;
        private User.KycStatus kycStatus;
        private User.UserRole userRole;
        private Boolean isActive;
        private LocalDateTime createdAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserProfileResponse {
        private Long userId;
        private String email;
        private String fullName;
        private String phoneNumber;
        private String cnicNumber;
        private String kycStatus;
        private String walletNumber;
        private BigDecimal balance;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateUserRequest {
        private String fullName;
        private String phoneNumber;
    }
}
