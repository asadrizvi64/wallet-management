package com.enterprise.wallet.repository;

import com.enterprise.wallet.entity.Wallet;
import com.enterprise.wallet.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Wallet Repository - Data access layer for Wallet entity
 */
@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    
    Optional<Wallet> findByWalletNumber(String walletNumber);

    Optional<Wallet> findByUser(User user);

    List<Wallet> findByUserId(Long userId);

    boolean existsByWalletNumber(String walletNumber);
}
