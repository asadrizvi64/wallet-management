package com.enterprise.wallet.dto;

import com.enterprise.wallet.model.Transaction;
import com.enterprise.wallet.model.Wallet;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// ========== Wallet DTOs ==========

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateWalletRequest {
    private Wallet.WalletType walletType = Wallet.WalletType.PERSONAL;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletResponse {
    private Long id;
    private String walletNumber;
    private BigDecimal balance;
    private String currency;
    private Wallet.WalletStatus walletStatus;
    private Wallet.WalletType walletType;
    private Long userId;
    private String userName;
    private LocalDateTime createdAt;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateWalletStatusRequest {
    @NotNull(message = "Wallet status is required")
    private Wallet.WalletStatus walletStatus;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletBalanceResponse {
    private Long walletId;
    private String walletNumber;
    private BigDecimal balance;
    private String currency;
    private Wallet.WalletStatus status;
}

// ========== Transaction DTOs ==========

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddMoneyRequest {
    @NotNull(message = "Wallet ID is required")
    private Long walletId;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.0", message = "Amount must be at least 1")
    private BigDecimal amount;
    
    @NotBlank(message = "Payment method is required")
    private String paymentMethod;
    
    private String description;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawMoneyRequest {
    @NotNull(message = "Wallet ID is required")
    private Long walletId;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.0", message = "Amount must be at least 1")
    private BigDecimal amount;
    
    @NotBlank(message = "Payment method is required")
    private String paymentMethod;
    
    private String description;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferMoneyRequest {
    @NotNull(message = "From wallet ID is required")
    private Long fromWalletId;
    
    @NotNull(message = "To wallet number is required")
    @NotBlank(message = "To wallet number cannot be blank")
    private String toWalletNumber;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.0", message = "Amount must be at least 1")
    private BigDecimal amount;
    
    private String description;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessPaymentRequest {
    @NotNull(message = "Wallet ID is required")
    private Long walletId;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.0", message = "Amount must be at least 1")
    private BigDecimal amount;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    private String merchantId;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private Long id;
    private String transactionRef;
    private Long walletId;
    private Transaction.TransactionType transactionType;
    private BigDecimal amount;
    private String currency;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private Transaction.TransactionStatus transactionStatus;
    private String description;
    private String recipientWalletNumber;
    private String paymentMethod;
    private BigDecimal transactionFee;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionHistoryResponse {
    private List<TransactionResponse> transactions;
    private Long totalCount;
    private BigDecimal totalCredits;
    private BigDecimal totalDebits;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefundTransactionRequest {
    @NotBlank(message = "Transaction reference is required")
    private String transactionRef;
    
    private String reason;
}
