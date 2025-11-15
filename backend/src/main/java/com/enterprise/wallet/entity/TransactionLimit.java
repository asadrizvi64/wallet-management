package com.enterprise.wallet.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction_limits")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionLimit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false, unique = true)
    private Wallet wallet;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "daily_limit", precision = 15, scale = 2, nullable = false)
    private BigDecimal dailyLimit;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "monthly_limit", precision = 15, scale = 2, nullable = false)
    private BigDecimal monthlyLimit;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "per_transaction_limit", precision = 15, scale = 2, nullable = false)
    private BigDecimal perTransactionLimit;

    @Column(name = "daily_spent", precision = 15, scale = 2)
    private BigDecimal dailySpent = BigDecimal.ZERO;

    @Column(name = "monthly_spent", precision = 15, scale = 2)
    private BigDecimal monthlySpent = BigDecimal.ZERO;

    @Column(name = "last_reset_date")
    private LocalDate lastResetDate;

    @Column(name = "monthly_reset_date")
    private LocalDate monthlyResetDate;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
