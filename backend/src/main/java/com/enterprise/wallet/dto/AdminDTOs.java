package com.enterprise.wallet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Admin DTOs - Data Transfer Objects for admin operations
 */
public class AdminDTOs {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DashboardStatsResponse {
        // User stats
        private Long totalUsers;
        private Long activeUsers;
        private Long pendingKyc;
        private Long verifiedKyc;

        // Wallet stats
        private Long totalWallets;
        private Long activeWallets;
        private BigDecimal totalBalance;

        // Transaction stats
        private Long totalTransactions;
        private Long completedTransactions;
        private Long pendingTransactions;
        private Long failedTransactions;
        private BigDecimal totalTransactionVolume;
        private BigDecimal totalRevenue;

        // Recent activity
        private Long recentUsers;
        private Long recentTransactions;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdminWalletResponse {
        private Long id;
        private String walletNumber;
        private Long userId;
        private String userFullName;
        private String userEmail;
        private BigDecimal balance;
        private String currency;
        private String walletStatus;
        private String walletType;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdminTransactionResponse {
        private Long id;
        private String transactionRef;
        private String walletNumber;
        private String userFullName;
        private String recipientWalletNumber;
        private String recipientFullName;
        private BigDecimal amount;
        private BigDecimal fee;
        private String currency;
        private String transactionType;
        private String transactionStatus;
        private String paymentMethod;
        private String description;
        private LocalDateTime createdAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdminPaymentMethodResponse {
        private Long id;
        private Long userId;
        private String userFullName;
        private String userEmail;
        private String paymentType;
        private String providerName;
        private String accountNumber;
        private String cardLastFour;
        private String cardBrand;
        private String status;
        private LocalDateTime createdAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateWalletStatusRequest {
        private String status; // ACTIVE, INACTIVE, FROZEN, BLOCKED
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefundRequest {
        private String reason;
    }
}
