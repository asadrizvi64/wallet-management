package com.enterprise.wallet.dto;

import com.enterprise.wallet.model.PaymentLink;
import com.enterprise.wallet.model.WalletNotification;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

// ========== Payment Link DTOs ==========

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentLinkRequest {
    @NotNull(message = "Wallet ID is required")
    private Long walletId;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.0", message = "Amount must be at least 1")
    private BigDecimal amount;
    
    private String description;
    
    private Integer expiryHours = 24; // Default 24 hours
}

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentLinkResponse {
    private Long id;
    private String linkCode;
    private String paymentUrl;
    private BigDecimal amount;
    private String description;
    private PaymentLink.PaymentStatus paymentStatus;
    private Boolean isUsed;
    private LocalDateTime expiryDate;
    private LocalDateTime createdAt;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyPaymentRequest {
    @NotBlank(message = "Link code is required")
    private String linkCode;
    
    @NotNull(message = "Payer wallet ID is required")
    private Long payerWalletId;
}

// ========== Transaction Limit DTOs ==========

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionLimitResponse {
    private Long walletId;
    private BigDecimal dailyLimit;
    private BigDecimal monthlyLimit;
    private BigDecimal perTransactionLimit;
    private BigDecimal dailySpent;
    private BigDecimal monthlySpent;
    private BigDecimal dailyRemaining;
    private BigDecimal monthlyRemaining;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLimitRequest {
    @NotNull(message = "Daily limit is required")
    @DecimalMin(value = "0", message = "Daily limit must be positive")
    private BigDecimal dailyLimit;
    
    @NotNull(message = "Monthly limit is required")
    @DecimalMin(value = "0", message = "Monthly limit must be positive")
    private BigDecimal monthlyLimit;
    
    @NotNull(message = "Per transaction limit is required")
    @DecimalMin(value = "0", message = "Per transaction limit must be positive")
    private BigDecimal perTransactionLimit;
}

// ========== Notification DTOs ==========

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private Long id;
    private WalletNotification.NotificationType notificationType;
    private String title;
    private String message;
    private Boolean isRead;
    private WalletNotification.Priority priority;
    private LocalDateTime createdAt;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationListResponse {
    private List<NotificationResponse> notifications;
    private Long unreadCount;
}

// ========== Statement/Report DTOs ==========

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatementRequest {
    @NotNull(message = "Wallet ID is required")
    private Long walletId;
    
    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;
    
    @NotNull(message = "End date is required")
    private LocalDateTime endDate;
    
    private String format = "PDF"; // PDF or CSV
}

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletStatementResponse {
    private String walletNumber;
    private String period;
    private BigDecimal openingBalance;
    private BigDecimal closingBalance;
    private BigDecimal totalCredits;
    private BigDecimal totalDebits;
    private Integer transactionCount;
    private List<TransactionResponse> transactions;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpendingAnalyticsResponse {
    private Long walletId;
    private String period;
    private BigDecimal totalSpent;
    private BigDecimal averageTransaction;
    private Integer transactionCount;
    private Map<String, BigDecimal> categoryWiseSpending;
    private Map<String, Integer> transactionTypeBreakdown;
}

// ========== Generic Response DTOs ==========

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private Boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp;
    
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, LocalDateTime.now());
    }
    
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, LocalDateTime.now());
    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private Boolean success = false;
    private String message;
    private String error;
    private LocalDateTime timestamp;
    private String path;
}
