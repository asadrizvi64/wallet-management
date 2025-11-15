package com.enterprise.wallet.repository;

import com.enterprise.wallet.entity.Transaction;
import com.enterprise.wallet.entity.Wallet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Transaction Repository - Data access layer for Transaction entity
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    Optional<Transaction> findByTransactionReference(String transactionReference);
    
    List<Transaction> findBySenderWalletOrReceiverWalletOrderByCreatedAtDesc(
        Wallet senderWallet, Wallet receiverWallet
    );
    
    Page<Transaction> findBySenderWalletOrReceiverWallet(
        Wallet senderWallet, Wallet receiverWallet, Pageable pageable
    );
    
    List<Transaction> findBySenderWalletAndCreatedAtBetween(
        Wallet wallet, LocalDateTime start, LocalDateTime end
    );
    
    @Query("SELECT t FROM Transaction t WHERE " +
           "(t.senderWallet = :wallet OR t.receiverWallet = :wallet) " +
           "AND t.createdAt BETWEEN :start AND :end " +
           "ORDER BY t.createdAt DESC")
    List<Transaction> findWalletTransactionsBetween(
        Wallet wallet, LocalDateTime start, LocalDateTime end
    );
}
