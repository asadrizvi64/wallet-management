package com.enterprise.wallet.controller;

import com.enterprise.wallet.dto.AdminDTOs.*;
import com.enterprise.wallet.dto.AdminDTOs.UpdateWalletStatusRequest;
import com.enterprise.wallet.dto.OtherDTOs.*;
import com.enterprise.wallet.dto.TransactionDTOs.*;
import com.enterprise.wallet.entity.*;
import com.enterprise.wallet.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Admin Controller - REST APIs for admin/superuser dashboard
 * Base URL: /api/v1/admin
 */
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPERUSER')")
public class AdminController {

    private final UserService userService;
    private final WalletService walletService;
    private final TransactionService transactionService;
    private final PaymentMethodService paymentMethodService;

    /**
     * API 1: GET /api/v1/admin/dashboard
     * Get dashboard statistics
     */
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getDashboardStats() {
        try {
            DashboardStatsResponse stats = new DashboardStatsResponse();

            // User statistics
            List<User> allUsers = userService.getAllUsers();
            stats.setTotalUsers((long) allUsers.size());
            stats.setActiveUsers(allUsers.stream().filter(User::getIsActive).count());
            stats.setPendingKyc(allUsers.stream()
                    .filter(u -> u.getKycStatus() == User.KycStatus.PENDING).count());
            stats.setVerifiedKyc(allUsers.stream()
                    .filter(u -> u.getKycStatus() == User.KycStatus.VERIFIED).count());

            // Wallet statistics
            List<Wallet> allWallets = walletService.getAllWallets();
            stats.setTotalWallets((long) allWallets.size());
            stats.setActiveWallets(allWallets.stream()
                    .filter(w -> w.getWalletStatus() == Wallet.WalletStatus.ACTIVE).count());

            BigDecimal totalBalance = allWallets.stream()
                    .map(Wallet::getBalance)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            stats.setTotalBalance(totalBalance);

            // Transaction statistics
            List<Transaction> allTransactions = transactionService.getAllTransactions();
            stats.setTotalTransactions((long) allTransactions.size());
            stats.setCompletedTransactions(allTransactions.stream()
                    .filter(t -> t.getTransactionStatus() == Transaction.TransactionStatus.COMPLETED).count());
            stats.setPendingTransactions(allTransactions.stream()
                    .filter(t -> t.getTransactionStatus() == Transaction.TransactionStatus.PENDING).count());
            stats.setFailedTransactions(allTransactions.stream()
                    .filter(t -> t.getTransactionStatus() == Transaction.TransactionStatus.FAILED).count());

            BigDecimal totalVolume = allTransactions.stream()
                    .filter(t -> t.getTransactionStatus() == Transaction.TransactionStatus.COMPLETED)
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            stats.setTotalTransactionVolume(totalVolume);

            BigDecimal totalFees = allTransactions.stream()
                    .filter(t -> t.getTransactionStatus() == Transaction.TransactionStatus.COMPLETED)
                    .map(t -> t.getTransactionFee() != null ? t.getTransactionFee() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            stats.setTotalRevenue(totalFees);

            // Recent activities
            stats.setRecentUsers(allUsers.stream()
                    .sorted((u1, u2) -> u2.getCreatedAt().compareTo(u1.getCreatedAt()))
                    .limit(5)
                    .count());

            stats.setRecentTransactions(allTransactions.stream()
                    .sorted((t1, t2) -> t2.getCreatedAt().compareTo(t1.getCreatedAt()))
                    .limit(10)
                    .count());

            return ResponseEntity.ok(
                    ApiResponse.success("Dashboard statistics retrieved successfully", stats)
            );

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error(e.getMessage())
            );
        }
    }

    /**
     * API 2: GET /api/v1/admin/wallets
     * Get all wallets
     */
    @GetMapping("/wallets")
    public ResponseEntity<ApiResponse<List<AdminWalletResponse>>> getAllWallets() {
        try {
            List<Wallet> wallets = walletService.getAllWallets();

            List<AdminWalletResponse> response = wallets.stream()
                    .map(wallet -> new AdminWalletResponse(
                            wallet.getId(),
                            wallet.getWalletNumber(),
                            wallet.getUser().getId(),
                            wallet.getUser().getFullName(),
                            wallet.getUser().getEmail(),
                            wallet.getBalance(),
                            wallet.getCurrency(),
                            wallet.getWalletStatus().toString(),
                            wallet.getWalletType().toString(),
                            wallet.getCreatedAt(),
                            wallet.getUpdatedAt()
                    ))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(
                    ApiResponse.success("Wallets retrieved successfully", response)
            );

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error(e.getMessage())
            );
        }
    }

    /**
     * API 3: PUT /api/v1/admin/wallets/{walletId}
     * Update wallet status
     */
    @PutMapping("/wallets/{walletId}")
    @PreAuthorize("hasRole('SUPERUSER')")
    public ResponseEntity<ApiResponse<String>> updateWalletStatus(
            @PathVariable Long walletId,
            @Valid @RequestBody AdminDTOs.UpdateWalletStatusRequest request) {

        try {
            Wallet.WalletStatus status = Wallet.WalletStatus.valueOf(request.getStatus());
            walletService.updateWalletStatusById(walletId, status);

            return ResponseEntity.ok(
                    ApiResponse.success("Wallet status updated successfully",
                            "Wallet " + walletId + " is now " + status)
            );

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error(e.getMessage())
            );
        }
    }

    /**
     * API 4: GET /api/v1/admin/transactions
     * Get all transactions
     */
    @GetMapping("/transactions")
    public ResponseEntity<ApiResponse<List<AdminTransactionResponse>>> getAllTransactions(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type) {

        try {
            List<Transaction> transactions = transactionService.getAllTransactions();

            // Filter by status if provided
            if (status != null && !status.isEmpty()) {
                Transaction.TransactionStatus txStatus = Transaction.TransactionStatus.valueOf(status);
                transactions = transactions.stream()
                        .filter(t -> t.getTransactionStatus() == txStatus)
                        .collect(Collectors.toList());
            }

            // Filter by type if provided
            if (type != null && !type.isEmpty()) {
                Transaction.TransactionType txType = Transaction.TransactionType.valueOf(type);
                transactions = transactions.stream()
                        .filter(t -> t.getTransactionType() == txType)
                        .collect(Collectors.toList());
            }

            List<AdminTransactionResponse> response = transactions.stream()
                    .sorted((t1, t2) -> t2.getCreatedAt().compareTo(t1.getCreatedAt()))
                    .map(tx -> new AdminTransactionResponse(
                            tx.getId(),
                            tx.getTransactionRef(),
                            tx.getWallet().getWalletNumber(),
                            tx.getWallet().getUser().getFullName(),
                            tx.getRecipientWallet() != null ? tx.getRecipientWallet().getWalletNumber() : null,
                            tx.getRecipientWallet() != null ? tx.getRecipientWallet().getUser().getFullName() : null,
                            tx.getAmount(),
                            tx.getTransactionFee(),
                            tx.getCurrency(),
                            tx.getTransactionType().toString(),
                            tx.getTransactionStatus().toString(),
                            tx.getPaymentMethod(),
                            tx.getDescription(),
                            tx.getCreatedAt()
                    ))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(
                    ApiResponse.success("Transactions retrieved successfully", response)
            );

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error(e.getMessage())
            );
        }
    }

    /**
     * API 5: POST /api/v1/admin/transactions/{transactionRef}/refund
     * Refund a transaction (SUPERUSER only)
     */
    @PostMapping("/transactions/{transactionRef}/refund")
    @PreAuthorize("hasRole('SUPERUSER')")
    public ResponseEntity<ApiResponse<String>> refundTransaction(
            @PathVariable String transactionRef,
            @Valid @RequestBody RefundRequest request) {

        try {
            RefundTransactionRequest refundRequest = new RefundTransactionRequest(
                    transactionRef,
                    request.getReason()
            );

            TransactionResponse refundedTx = transactionService.refundTransaction(refundRequest);

            return ResponseEntity.ok(
                    ApiResponse.success("Transaction refunded successfully",
                            "Refund transaction: " + refundedTx.getTransactionRef())
            );

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error(e.getMessage())
            );
        }
    }

    /**
     * API 6: GET /api/v1/admin/payment-methods
     * Get all payment methods
     */
    @GetMapping("/payment-methods")
    public ResponseEntity<ApiResponse<List<AdminPaymentMethodResponse>>> getAllPaymentMethods() {
        try {
            List<PaymentMethod> paymentMethods = paymentMethodService.getAllPaymentMethods();

            List<AdminPaymentMethodResponse> response = paymentMethods.stream()
                    .map(pm -> new AdminPaymentMethodResponse(
                            pm.getId(),
                            pm.getUser().getId(),
                            pm.getUser().getFullName(),
                            pm.getUser().getEmail(),
                            pm.getPaymentType().toString(),
                            pm.getProviderName(),
                            pm.getAccountNumber(),
                            pm.getCardLastFour(),
                            pm.getCardBrand(),
                            pm.getStatus().toString(),
                            pm.getCreatedAt()
                    ))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(
                    ApiResponse.success("Payment methods retrieved successfully", response)
            );

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error(e.getMessage())
            );
        }
    }

    /**
     * API 7: DELETE /api/v1/admin/payment-methods/{paymentMethodId}
     * Delete payment method (SUPERUSER only)
     */
    @DeleteMapping("/payment-methods/{paymentMethodId}")
    @PreAuthorize("hasRole('SUPERUSER')")
    public ResponseEntity<ApiResponse<String>> deletePaymentMethod(
            @PathVariable Long paymentMethodId) {

        try {
            paymentMethodService.deletePaymentMethod(paymentMethodId);

            return ResponseEntity.ok(
                    ApiResponse.success("Payment method deleted successfully",
                            "Payment method " + paymentMethodId + " has been removed")
            );

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error(e.getMessage())
            );
        }
    }

    /**
     * API 8: DELETE /api/v1/admin/users/{userId}
     * Delete user (SUPERUSER only)
     */
    @DeleteMapping("/users/{userId}")
    @PreAuthorize("hasRole('SUPERUSER')")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable Long userId) {
        try {
            userService.deleteUser(userId);

            return ResponseEntity.ok(
                    ApiResponse.success("User deleted successfully",
                            "User " + userId + " and all associated data have been removed")
            );

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error(e.getMessage())
            );
        }
    }
}
