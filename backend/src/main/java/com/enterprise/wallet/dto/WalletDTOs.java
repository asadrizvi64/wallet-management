package com.enterprise.wallet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * Data Transfer Objects for API Communication
 */

// ============= USER DTOs =============

@Data
@NoArgsConstructor
@AllArgsConstructor
class RegisterRequest {
    private String email;
    private String password;
    private String fullName;
    private String phoneNumber;
    private String cnicNumber;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class LoginRequest {
    private String email;
    private String password;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class LoginResponse {
    private Long userId;
    private String email;
    private String fullName;
    private String token;
    private String walletNumber;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class UserProfileResponse {
    private Long userId;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String cnicNumber;
    private String kycStatus;
    private String walletNumber;
    private BigDecimal balance;
}

// ============= WALLET DTOs =============

@Data
@NoArgsConstructor
@AllArgsConstructor
class WalletResponse {
    private Long walletId;
    private String walletNumber;
    private BigDecimal balance;
    private String currency;
    private String status;
    private BigDecimal dailyLimit;
    private BigDecimal monthlyLimit;
    private BigDecimal dailySpent;
    private BigDecimal monthlySpent;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class AddMoneyRequest {
    private BigDecimal amount;
    private String paymentMethod; // CARD, BANK_TRANSFER, etc.
    private String paymentGatewayRef;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class WithdrawRequest {
    private BigDecimal amount;
    private String bankAccount;
    private String ifscCode;
}

// ============= TRANSACTION DTOs =============

@Data
@NoArgsConstructor
@AllArgsConstructor
class TransferRequest {
    private String recipientWalletNumber;
    private BigDecimal amount;
    private String description;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class TransactionResponse {
    private Long transactionId;
    private String transactionReference;
    private String senderWalletNumber;
    private String receiverWalletNumber;
    private BigDecimal amount;
    private BigDecimal fee;
    private String type;
    private String status;
    private String description;
    private String createdAt;
    private String completedAt;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class PaymentRequest {
    private String merchantId;
    private BigDecimal amount;
    private String description;
    private String orderId;
}

// ============= COMMON DTOs =============

@Data
@NoArgsConstructor
@AllArgsConstructor
class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private String timestamp;
    
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, message, data, 
            java.time.LocalDateTime.now().toString());
    }
    
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, 
            java.time.LocalDateTime.now().toString());
    }
}
