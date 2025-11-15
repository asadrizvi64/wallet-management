package com.enterprise.wallet.repository;

import com.enterprise.wallet.entity.PaymentLink;
import com.enterprise.wallet.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Payment Link Repository - Data access layer for PaymentLink entity
 */
@Repository
public interface PaymentLinkRepository extends JpaRepository<PaymentLink, Long> {

    Optional<PaymentLink> findByLinkCode(String linkCode);

    List<PaymentLink> findByWalletIdOrderByCreatedAtDesc(Long walletId);

    List<PaymentLink> findByWalletAndPaymentStatus(Wallet wallet, PaymentLink.PaymentStatus status);

    @Query("SELECT p FROM PaymentLink p WHERE p.wallet.id = :walletId " +
           "AND p.createdAt BETWEEN :start AND :end " +
           "ORDER BY p.createdAt DESC")
    List<PaymentLink> findByWalletIdAndCreatedAtBetween(
        Long walletId, LocalDateTime start, LocalDateTime end
    );

    @Query("SELECT p FROM PaymentLink p WHERE p.expiryDate < :now " +
           "AND p.paymentStatus = 'PENDING' AND p.isUsed = false")
    List<PaymentLink> findExpiredLinks(LocalDateTime now);

    boolean existsByLinkCode(String linkCode);
}
