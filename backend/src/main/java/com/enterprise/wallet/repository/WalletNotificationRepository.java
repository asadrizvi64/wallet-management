package com.enterprise.wallet.repository;

import com.enterprise.wallet.entity.Wallet;
import com.enterprise.wallet.entity.WalletNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Wallet Notification Repository - Data access layer for WalletNotification entity
 */
@Repository
public interface WalletNotificationRepository extends JpaRepository<WalletNotification, Long> {

    List<WalletNotification> findByWalletIdOrderByCreatedAtDesc(Long walletId);

    Page<WalletNotification> findByWalletIdOrderByCreatedAtDesc(Long walletId, Pageable pageable);

    List<WalletNotification> findByWalletAndIsReadFalseOrderByCreatedAtDesc(Wallet wallet);

    long countByWalletIdAndIsReadFalse(Long walletId);

    List<WalletNotification> findByWalletIdAndNotificationTypeOrderByCreatedAtDesc(
        Long walletId, WalletNotification.NotificationType notificationType
    );

    @Query("SELECT n FROM WalletNotification n WHERE n.wallet.id = :walletId " +
           "AND n.createdAt BETWEEN :start AND :end " +
           "ORDER BY n.createdAt DESC")
    List<WalletNotification> findByWalletIdAndCreatedAtBetween(
        Long walletId, LocalDateTime start, LocalDateTime end
    );

    void deleteByWalletId(Long walletId);
}
