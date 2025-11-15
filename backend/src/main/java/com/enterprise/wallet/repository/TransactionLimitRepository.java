package com.enterprise.wallet.repository;

import com.enterprise.wallet.entity.TransactionLimit;
import com.enterprise.wallet.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Transaction Limit Repository - Data access layer for TransactionLimit entity
 */
@Repository
public interface TransactionLimitRepository extends JpaRepository<TransactionLimit, Long> {

    Optional<TransactionLimit> findByWallet(Wallet wallet);

    Optional<TransactionLimit> findByWalletId(Long walletId);

    boolean existsByWalletId(Long walletId);

    void deleteByWalletId(Long walletId);
}
