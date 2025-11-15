package com.enterprise.wallet.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "wallets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Wallet {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @NotBlank(message = "Wallet number is required")
    @Column(name = "wallet_number", unique = true, nullable = false, length = 20)
    private String walletNumber;
    
    @NotNull(message = "Balance is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Balance cannot be negative")
    @Column(precision = 15, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;
    
    @Column(length = 3)
    private String currency = "PKR";
    
    @Enumerated(EnumType.STRING)
    @Column(name = "wallet_status", length = 20)
    private WalletStatus walletStatus = WalletStatus.ACTIVE;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "wallet_type", length = 20)
    private WalletType walletType = WalletType.PERSONAL;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Transaction> transactions = new HashSet<>();
    
    @OneToOne(mappedBy = "wallet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private TransactionLimit transactionLimit;
    
    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<WalletNotification> notifications = new HashSet<>();
    
    public enum WalletStatus {
        ACTIVE, INACTIVE, FROZEN, BLOCKED
    }
    
    public enum WalletType {
        PERSONAL, BUSINESS, SAVINGS
    }
}
