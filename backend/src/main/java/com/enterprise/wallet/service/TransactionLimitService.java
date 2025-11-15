package com.enterprise.wallet.service;

import com.enterprise.wallet.dto.*;
import com.enterprise.wallet.entity.*;
import com.enterprise.wallet.exception.ResourceNotFoundException;
import com.enterprise.wallet.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class TransactionLimitService {

    private final TransactionLimitRepository limitRepository;
    private final WalletRepository walletRepository;

    public TransactionLimitResponse getTransactionLimits(Long walletId) {
        TransactionLimit limit = limitRepository.findByWalletId(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction limits not configured"));

        return mapToResponse(limit);
    }

    public TransactionLimitResponse updateTransactionLimits(Long walletId, UpdateLimitRequest request) {
        log.info("Updating transaction limits for wallet ID: {}", walletId);

        TransactionLimit limit = limitRepository.findByWalletId(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction limits not configured"));

        limit.setDailyLimit(request.getDailyLimit());
        limit.setMonthlyLimit(request.getMonthlyLimit());
        limit.setPerTransactionLimit(request.getPerTransactionLimit());

        TransactionLimit updated = limitRepository.save(limit);

        log.info("Transaction limits updated for wallet ID: {}", walletId);
        return mapToResponse(updated);
    }

    public TransactionLimitResponse createDefaultLimits(Long walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));

        if (limitRepository.existsByWalletId(walletId)) {
            throw new RuntimeException("Transaction limits already exist for this wallet");
        }

        TransactionLimit limit = new TransactionLimit();
        limit.setWallet(wallet);
        limit.setDailyLimit(new BigDecimal("50000"));
        limit.setMonthlyLimit(new BigDecimal("500000"));
        limit.setPerTransactionLimit(new BigDecimal("25000"));
        limit.setDailySpent(BigDecimal.ZERO);
        limit.setMonthlySpent(BigDecimal.ZERO);
        limit.setLastResetDate(java.time.LocalDate.now());
        limit.setMonthlyResetDate(java.time.LocalDate.now());

        TransactionLimit saved = limitRepository.save(limit);

        log.info("Default transaction limits created for wallet ID: {}", walletId);
        return mapToResponse(saved);
    }

    private TransactionLimitResponse mapToResponse(TransactionLimit limit) {
        BigDecimal dailyRemaining = limit.getDailyLimit().subtract(limit.getDailySpent());
        BigDecimal monthlyRemaining = limit.getMonthlyLimit().subtract(limit.getMonthlySpent());

        return new TransactionLimitResponse(
                limit.getWallet().getId(),
                limit.getDailyLimit(),
                limit.getMonthlyLimit(),
                limit.getPerTransactionLimit(),
                limit.getDailySpent(),
                limit.getMonthlySpent(),
                dailyRemaining,
                monthlyRemaining
        );
    }
}
