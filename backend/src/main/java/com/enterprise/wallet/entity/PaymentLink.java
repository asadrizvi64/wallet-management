package com.enterprise.wallet.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_links")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentLink {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;
    
    @Column(name = "link_code", unique = true, nullable = false, length = 50)
    private String linkCode;
    
    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal amount;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;
    
    @Column(name = "is_used")
    private Boolean isUsed = false;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", length = 20)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paid_by_wallet_id")
    private Wallet paidByWallet;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "used_at")
    private LocalDateTime usedAt;
    
    public enum PaymentStatus {
        PENDING, COMPLETED, EXPIRED, CANCELLED
    }
}
