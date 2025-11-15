package com.enterprise.wallet.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Transaction reference is required")
    @Column(name = "transaction_ref", unique = true, nullable = false, length = 50)
    private String transactionRef;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 20)
    private TransactionType transactionType;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal amount;
    
    @Column(length = 3)
    private String currency = "PKR";
    
    @NotNull
    @Column(name = "balance_before", precision = 15, scale = 2, nullable = false)
    private BigDecimal balanceBefore;
    
    @NotNull
    @Column(name = "balance_after", precision = 15, scale = 2, nullable = false)
    private BigDecimal balanceAfter;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_status", length = 20)
    private TransactionStatus transactionStatus = TransactionStatus.PENDING;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_wallet_id")
    private Wallet recipientWallet;
    
    @Column(name = "payment_method", length = 50)
    private String paymentMethod;
    
    @Column(name = "transaction_fee", precision = 10, scale = 2)
    private BigDecimal transactionFee = BigDecimal.ZERO;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    public enum TransactionType {
        CREDIT, DEBIT, TRANSFER_IN, TRANSFER_OUT, PAYMENT, REFUND, WITHDRAWAL, TOP_UP
    }
    
    public enum TransactionStatus {
        PENDING, COMPLETED, FAILED, CANCELLED, REFUNDED
    }
}
