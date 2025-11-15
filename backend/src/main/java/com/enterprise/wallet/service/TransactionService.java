package com.enterprise.wallet.service;

import com.enterprise.wallet.dto.TransactionDTOs.*;
import com.enterprise.wallet.exception.InsufficientBalanceException;
import com.enterprise.wallet.exception.ResourceNotFoundException;
import com.enterprise.wallet.exception.TransactionException;
import com.enterprise.wallet.entity.*;
import com.enterprise.wallet.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class TransactionService {
    
    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final TransactionLimitRepository limitRepository;
    private final WalletNotificationRepository notificationRepository;
    private final WalletService walletService;
    
    // Function 6: Add Money (Top-up)
    public TransactionResponse addMoney(AddMoneyRequest request) {
        log.info("Adding money to wallet ID: {}", request.getWalletId());
        
        Wallet wallet = walletService.getWalletById(request.getWalletId());
        
        if (wallet.getWalletStatus() != Wallet.WalletStatus.ACTIVE) {
            throw new TransactionException("Wallet is not active");
        }
        
        // Check transaction limit
        checkTransactionLimit(wallet, request.getAmount());
        
        // Create transaction
        String transactionRef = generateTransactionRef();
        BigDecimal balanceBefore = wallet.getBalance();
        BigDecimal balanceAfter = balanceBefore.add(request.getAmount());
        
        Transaction transaction = new Transaction();
        transaction.setTransactionRef(transactionRef);
        transaction.setWallet(wallet);
        transaction.setTransactionType(Transaction.TransactionType.TOP_UP);
        transaction.setAmount(request.getAmount());
        transaction.setBalanceBefore(balanceBefore);
        transaction.setBalanceAfter(balanceAfter);
        transaction.setDescription(request.getDescription());
        transaction.setPaymentMethod(request.getPaymentMethod());
        transaction.setTransactionStatus(Transaction.TransactionStatus.COMPLETED);
        transaction.setCompletedAt(LocalDateTime.now());
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        // Update wallet balance
        wallet.setBalance(balanceAfter);
        walletRepository.save(wallet);
        
        // Update spending limits
        updateSpendingLimits(wallet, request.getAmount());
        
        // Send notification
        createNotification(wallet, "Money Added", 
                "PKR " + request.getAmount() + " has been added to your wallet");
        
        log.info("Money added successfully. Transaction ref: {}", transactionRef);
        return mapToTransactionResponse(savedTransaction);
    }
    
    // Function 7: Withdraw Money
    public TransactionResponse withdrawMoney(WithdrawMoneyRequest request) {
        log.info("Withdrawing money from wallet ID: {}", request.getWalletId());
        
        Wallet wallet = walletService.getWalletById(request.getWalletId());
        
        if (wallet.getWalletStatus() != Wallet.WalletStatus.ACTIVE) {
            throw new TransactionException("Wallet is not active");
        }
        
        // Check sufficient balance
        if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }
        
        // Check transaction limit
        checkTransactionLimit(wallet, request.getAmount());
        
        String transactionRef = generateTransactionRef();
        BigDecimal balanceBefore = wallet.getBalance();
        BigDecimal balanceAfter = balanceBefore.subtract(request.getAmount());
        
        Transaction transaction = new Transaction();
        transaction.setTransactionRef(transactionRef);
        transaction.setWallet(wallet);
        transaction.setTransactionType(Transaction.TransactionType.WITHDRAWAL);
        transaction.setAmount(request.getAmount());
        transaction.setBalanceBefore(balanceBefore);
        transaction.setBalanceAfter(balanceAfter);
        transaction.setDescription(request.getDescription());
        transaction.setPaymentMethod(request.getPaymentMethod());
        transaction.setTransactionStatus(Transaction.TransactionStatus.COMPLETED);
        transaction.setCompletedAt(LocalDateTime.now());
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        // Update wallet balance
        wallet.setBalance(balanceAfter);
        walletRepository.save(wallet);
        
        // Update spending limits
        updateSpendingLimits(wallet, request.getAmount());
        
        // Send notification
        createNotification(wallet, "Money Withdrawn", 
                "PKR " + request.getAmount() + " has been withdrawn from your wallet");
        
        log.info("Money withdrawn successfully. Transaction ref: {}", transactionRef);
        return mapToTransactionResponse(savedTransaction);
    }
    
    // Function 8: Transfer Money
    public TransactionResponse transferMoney(TransferMoneyRequest request) {
        log.info("Transferring money from wallet ID: {} to wallet: {}", 
                request.getFromWalletId(), request.getToWalletNumber());
        
        Wallet fromWallet = walletService.getWalletById(request.getFromWalletId());
        Wallet toWallet = walletService.getWalletByNumber(request.getToWalletNumber());
        
        // Validations
        if (fromWallet.getWalletStatus() != Wallet.WalletStatus.ACTIVE) {
            throw new TransactionException("Source wallet is not active");
        }
        
        if (toWallet.getWalletStatus() != Wallet.WalletStatus.ACTIVE) {
            throw new TransactionException("Destination wallet is not active");
        }
        
        if (fromWallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }
        
        checkTransactionLimit(fromWallet, request.getAmount());
        
        // Create transfer-out transaction
        String transactionRef = generateTransactionRef();
        BigDecimal fromBalanceBefore = fromWallet.getBalance();
        BigDecimal fromBalanceAfter = fromBalanceBefore.subtract(request.getAmount());
        
        Transaction transferOut = new Transaction();
        transferOut.setTransactionRef(transactionRef + "-OUT");
        transferOut.setWallet(fromWallet);
        transferOut.setTransactionType(Transaction.TransactionType.TRANSFER_OUT);
        transferOut.setAmount(request.getAmount());
        transferOut.setBalanceBefore(fromBalanceBefore);
        transferOut.setBalanceAfter(fromBalanceAfter);
        transferOut.setDescription(request.getDescription());
        transferOut.setRecipientWallet(toWallet);
        transferOut.setTransactionStatus(Transaction.TransactionStatus.COMPLETED);
        transferOut.setCompletedAt(LocalDateTime.now());
        
        transactionRepository.save(transferOut);
        
        // Create transfer-in transaction
        BigDecimal toBalanceBefore = toWallet.getBalance();
        BigDecimal toBalanceAfter = toBalanceBefore.add(request.getAmount());
        
        Transaction transferIn = new Transaction();
        transferIn.setTransactionRef(transactionRef + "-IN");
        transferIn.setWallet(toWallet);
        transferIn.setTransactionType(Transaction.TransactionType.TRANSFER_IN);
        transferIn.setAmount(request.getAmount());
        transferIn.setBalanceBefore(toBalanceBefore);
        transferIn.setBalanceAfter(toBalanceAfter);
        transferIn.setDescription(request.getDescription());
        transferIn.setTransactionStatus(Transaction.TransactionStatus.COMPLETED);
        transferIn.setCompletedAt(LocalDateTime.now());
        
        transactionRepository.save(transferIn);
        
        // Update wallet balances
        fromWallet.setBalance(fromBalanceAfter);
        toWallet.setBalance(toBalanceAfter);
        walletRepository.save(fromWallet);
        walletRepository.save(toWallet);
        
        // Update spending limits
        updateSpendingLimits(fromWallet, request.getAmount());
        
        // Send notifications
        createNotification(fromWallet, "Money Transferred", 
                "PKR " + request.getAmount() + " transferred to " + toWallet.getWalletNumber());
        createNotification(toWallet, "Money Received", 
                "PKR " + request.getAmount() + " received from " + fromWallet.getWalletNumber());
        
        log.info("Transfer completed successfully. Ref: {}", transactionRef);
        return mapToTransactionResponse(transferOut);
    }
    
    // Function 9: Get Transaction Details
    public TransactionResponse getTransactionDetails(String transactionRef) {
        Transaction transaction = transactionRepository.findByTransactionRef(transactionRef)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
        return mapToTransactionResponse(transaction);
    }
    
    // Function 10: Get Transaction History
    public TransactionHistoryResponse getTransactionHistory(Long walletId) {
        List<Transaction> transactions = transactionRepository.findByWalletIdOrderByCreatedAtDesc(walletId);
        
        BigDecimal totalCredits = transactions.stream()
                .filter(t -> t.getTransactionType() == Transaction.TransactionType.CREDIT ||
                            t.getTransactionType() == Transaction.TransactionType.TOP_UP ||
                            t.getTransactionType() == Transaction.TransactionType.TRANSFER_IN)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalDebits = transactions.stream()
                .filter(t -> t.getTransactionType() == Transaction.TransactionType.DEBIT ||
                            t.getTransactionType() == Transaction.TransactionType.PAYMENT ||
                            t.getTransactionType() == Transaction.TransactionType.WITHDRAWAL ||
                            t.getTransactionType() == Transaction.TransactionType.TRANSFER_OUT)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        List<TransactionResponse> response = transactions.stream()
                .map(this::mapToTransactionResponse)
                .collect(Collectors.toList());
        
        return new TransactionHistoryResponse(response, (long) transactions.size(), totalCredits, totalDebits);
    }

    // Function 10b: Get Transaction History by Wallet Number
    public TransactionHistoryResponse getTransactionHistoryByWalletNumber(String walletNumber) {
        Wallet wallet = walletService.getWalletByNumber(walletNumber);
        return getTransactionHistory(wallet.getId());
    }

    // Function 11: Cancel Transaction
    public TransactionResponse cancelTransaction(String transactionRef) {
        Transaction transaction = transactionRepository.findByTransactionRef(transactionRef)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
        
        if (transaction.getTransactionStatus() != Transaction.TransactionStatus.PENDING) {
            throw new TransactionException("Only pending transactions can be cancelled");
        }
        
        transaction.setTransactionStatus(Transaction.TransactionStatus.CANCELLED);
        Transaction updated = transactionRepository.save(transaction);
        
        createNotification(transaction.getWallet(), "Transaction Cancelled", 
                "Transaction " + transactionRef + " has been cancelled");
        
        return mapToTransactionResponse(updated);
    }
    
    // Function 12: Refund Transaction
    public TransactionResponse refundTransaction(RefundTransactionRequest request) {
        Transaction originalTransaction = transactionRepository.findByTransactionRef(request.getTransactionRef())
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
        
        if (originalTransaction.getTransactionStatus() != Transaction.TransactionStatus.COMPLETED) {
            throw new TransactionException("Only completed transactions can be refunded");
        }
        
        Wallet wallet = originalTransaction.getWallet();
        String refundRef = generateTransactionRef();
        BigDecimal balanceBefore = wallet.getBalance();
        BigDecimal balanceAfter = balanceBefore.add(originalTransaction.getAmount());
        
        Transaction refund = new Transaction();
        refund.setTransactionRef(refundRef);
        refund.setWallet(wallet);
        refund.setTransactionType(Transaction.TransactionType.REFUND);
        refund.setAmount(originalTransaction.getAmount());
        refund.setBalanceBefore(balanceBefore);
        refund.setBalanceAfter(balanceAfter);
        refund.setDescription("Refund for " + request.getTransactionRef() + ". Reason: " + request.getReason());
        refund.setTransactionStatus(Transaction.TransactionStatus.COMPLETED);
        refund.setCompletedAt(LocalDateTime.now());
        
        Transaction savedRefund = transactionRepository.save(refund);
        
        // Update original transaction status
        originalTransaction.setTransactionStatus(Transaction.TransactionStatus.REFUNDED);
        transactionRepository.save(originalTransaction);
        
        // Update wallet balance
        wallet.setBalance(balanceAfter);
        walletRepository.save(wallet);
        
        createNotification(wallet, "Refund Processed", 
                "PKR " + originalTransaction.getAmount() + " has been refunded to your wallet");
        
        return mapToTransactionResponse(savedRefund);
    }
    
    // Function 13: Process Payment
    public TransactionResponse processPayment(ProcessPaymentRequest request) {
        Wallet wallet = walletService.getWalletById(request.getWalletId());
        
        if (wallet.getWalletStatus() != Wallet.WalletStatus.ACTIVE) {
            throw new TransactionException("Wallet is not active");
        }
        
        if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }
        
        checkTransactionLimit(wallet, request.getAmount());
        
        String transactionRef = generateTransactionRef();
        BigDecimal balanceBefore = wallet.getBalance();
        BigDecimal balanceAfter = balanceBefore.subtract(request.getAmount());
        
        Transaction payment = new Transaction();
        payment.setTransactionRef(transactionRef);
        payment.setWallet(wallet);
        payment.setTransactionType(Transaction.TransactionType.PAYMENT);
        payment.setAmount(request.getAmount());
        payment.setBalanceBefore(balanceBefore);
        payment.setBalanceAfter(balanceAfter);
        payment.setDescription(request.getDescription());
        payment.setTransactionStatus(Transaction.TransactionStatus.COMPLETED);
        payment.setCompletedAt(LocalDateTime.now());
        
        Transaction savedPayment = transactionRepository.save(payment);
        
        wallet.setBalance(balanceAfter);
        walletRepository.save(wallet);
        
        updateSpendingLimits(wallet, request.getAmount());
        
        createNotification(wallet, "Payment Completed", 
                "Payment of PKR " + request.getAmount() + " completed successfully");
        
        return mapToTransactionResponse(savedPayment);
    }
    
    // Helper Methods
    private String generateTransactionRef() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 15).toUpperCase();
    }
    
    private void checkTransactionLimit(Wallet wallet, BigDecimal amount) {
        // Auto-create transaction limits if not configured
        TransactionLimit limit = limitRepository.findByWalletId(wallet.getId())
                .orElseGet(() -> createDefaultTransactionLimits(wallet));

        // Reset if needed
        if (!limit.getLastResetDate().equals(java.time.LocalDate.now())) {
            limit.setDailySpent(BigDecimal.ZERO);
            limit.setLastResetDate(java.time.LocalDate.now());
        }

        if (amount.compareTo(limit.getPerTransactionLimit()) > 0) {
            throw new TransactionException("Amount exceeds per transaction limit");
        }

        if (limit.getDailySpent().add(amount).compareTo(limit.getDailyLimit()) > 0) {
            throw new TransactionException("Daily limit exceeded");
        }

        if (limit.getMonthlySpent().add(amount).compareTo(limit.getMonthlyLimit()) > 0) {
            throw new TransactionException("Monthly limit exceeded");
        }
    }
    
    private void updateSpendingLimits(Wallet wallet, BigDecimal amount) {
        // Auto-create transaction limits if not configured
        TransactionLimit limit = limitRepository.findByWalletId(wallet.getId())
                .orElseGet(() -> createDefaultTransactionLimits(wallet));

        limit.setDailySpent(limit.getDailySpent().add(amount));
        limit.setMonthlySpent(limit.getMonthlySpent().add(amount));
        limitRepository.save(limit);
    }
    
    private TransactionLimit createDefaultTransactionLimits(Wallet wallet) {
        log.info("Creating default transaction limits for existing wallet ID: {}", wallet.getId());

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
        log.info("Default transaction limits created successfully for wallet ID: {}", wallet.getId());

        return saved;
    }

    private void createNotification(Wallet wallet, String title, String message) {
        WalletNotification notification = new WalletNotification();
        notification.setWallet(wallet);
        notification.setNotificationType(WalletNotification.NotificationType.TRANSACTION);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setPriority(WalletNotification.Priority.MEDIUM);
        notificationRepository.save(notification);
    }
    
    private TransactionResponse mapToTransactionResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getTransactionRef(),
                transaction.getWallet().getId(),
                transaction.getTransactionType(),
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getBalanceBefore(),
                transaction.getBalanceAfter(),
                transaction.getTransactionStatus(),
                transaction.getDescription(),
                transaction.getRecipientWallet() != null ? transaction.getRecipientWallet().getWalletNumber() : null,
                transaction.getPaymentMethod(),
                transaction.getTransactionFee(),
                transaction.getCreatedAt(),
                transaction.getCompletedAt()
        );
    }
}
