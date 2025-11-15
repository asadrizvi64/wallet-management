package com.enterprise.wallet.service;

import com.enterprise.wallet.dto.TransactionDTOs.*;
import com.enterprise.wallet.exception.ResourceNotFoundException;
import com.enterprise.wallet.exception.WalletException;
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
public class WalletService {
    
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionLimitRepository limitRepository;
    private final WalletNotificationRepository notificationRepository;
    
    // Function 1: Create Wallet
    public WalletResponse createWallet(Long userId, CreateWalletRequest request) {
        log.info("Creating wallet for user ID: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Check if user already has a wallet of this type
        List<Wallet> existingWallets = walletRepository.findByUserId(userId);
        if (existingWallets.stream().anyMatch(w -> w.getWalletType() == request.getWalletType())) {
            throw new WalletException("Wallet of type " + request.getWalletType() + " already exists");
        }
        
        // Generate unique wallet number
        String walletNumber = generateWalletNumber();
        
        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setWalletNumber(walletNumber);
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setWalletStatus(Wallet.WalletStatus.ACTIVE);
        wallet.setWalletType(request.getWalletType());
        
        Wallet savedWallet = walletRepository.save(wallet);
        
        // Create default transaction limits
        createDefaultLimits(savedWallet);
        
        // Send notification
        createNotification(savedWallet, "Wallet Created", 
                "Your wallet has been successfully created and is ready to use!");
        
        log.info("Wallet created successfully: {}", walletNumber);
        return mapToWalletResponse(savedWallet);
    }
    
    // Function 2: Get Wallet Details
    public WalletResponse getWalletDetails(Long walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));
        return mapToWalletResponse(wallet);
    }
    
    // Function 3: Get Wallet Balance
    public WalletBalanceResponse getWalletBalance(Long walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));
        
        return new WalletBalanceResponse(
                wallet.getId(),
                wallet.getWalletNumber(),
                wallet.getBalance(),
                wallet.getCurrency(),
                wallet.getWalletStatus()
        );
    }
    
    // Function 4: Update Wallet Status
    public WalletResponse updateWalletStatus(Long walletId, UpdateWalletStatusRequest request) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));
        
        Wallet.WalletStatus oldStatus = wallet.getWalletStatus();
        wallet.setWalletStatus(request.getWalletStatus());
        Wallet updated = walletRepository.save(wallet);
        
        // Notify user about status change
        createNotification(wallet, "Wallet Status Changed", 
                "Your wallet status has been updated from " + oldStatus + " to " + request.getWalletStatus());
        
        log.info("Wallet {} status updated to {}", walletId, request.getWalletStatus());
        return mapToWalletResponse(updated);
    }
    
    // Function 5: Get Wallet History
    public List<WalletResponse> getUserWallets(Long userId) {
        return walletRepository.findByUserId(userId).stream()
                .map(this::mapToWalletResponse)
                .collect(Collectors.toList());
    }
    
    // Helper Methods
    private String generateWalletNumber() {
        return "WLT-" + UUID.randomUUID().toString().substring(0, 15).toUpperCase();
    }
    
    private void createDefaultLimits(Wallet wallet) {
        TransactionLimit limit = new TransactionLimit();
        limit.setWallet(wallet);
        limit.setDailyLimit(new BigDecimal("50000"));
        limit.setMonthlyLimit(new BigDecimal("500000"));
        limit.setPerTransactionLimit(new BigDecimal("25000"));
        limit.setDailySpent(BigDecimal.ZERO);
        limit.setMonthlySpent(BigDecimal.ZERO);
        limit.setLastResetDate(java.time.LocalDate.now());
        limitRepository.save(limit);
    }
    
    private void createNotification(Wallet wallet, String title, String message) {
        WalletNotification notification = new WalletNotification();
        notification.setWallet(wallet);
        notification.setNotificationType(WalletNotification.NotificationType.SYSTEM);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setPriority(WalletNotification.Priority.MEDIUM);
        notificationRepository.save(notification);
    }
    
    private WalletResponse mapToWalletResponse(Wallet wallet) {
        return new WalletResponse(
                wallet.getId(),
                wallet.getWalletNumber(),
                wallet.getBalance(),
                wallet.getCurrency(),
                wallet.getWalletStatus(),
                wallet.getWalletType(),
                wallet.getUser().getId(),
                wallet.getUser().getFullName(),
                wallet.getCreatedAt()
        );
    }
    
    public Wallet getWalletById(Long walletId) {
        return walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));
    }
    
    public Wallet getWalletByNumber(String walletNumber) {
        return walletRepository.findByWalletNumber(walletNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found with number: " + walletNumber));
    }

    public Wallet getWalletByUserId(Long userId) {
        List<Wallet> wallets = walletRepository.findByUserId(userId);
        if (wallets.isEmpty()) {
            throw new ResourceNotFoundException("No wallet found for user ID: " + userId);
        }
        // Return the first wallet (primary wallet)
        return wallets.get(0);
    }

    // Get Wallet Details by Wallet Number
    public WalletResponse getWalletDetailsByNumber(String walletNumber) {
        Wallet wallet = getWalletByNumber(walletNumber);
        return mapToWalletResponse(wallet);
    }

    // Add Money by Wallet Number (Convenience method)
    public Object addMoneyByWalletNumber(String walletNumber, java.util.Map<String, Object> request) {
        Wallet wallet = getWalletByNumber(walletNumber);

        // Extract parameters from request
        BigDecimal amount = new BigDecimal(request.get("amount").toString());
        String paymentMethod = (String) request.getOrDefault("paymentMethod", "CARD");

        // Store balance before
        BigDecimal balanceBefore = wallet.getBalance();
        BigDecimal balanceAfter = balanceBefore.add(amount);

        // Create transaction
        Transaction transaction = new Transaction();
        transaction.setWallet(wallet);
        transaction.setRecipientWallet(wallet);
        transaction.setAmount(amount);
        transaction.setTransactionType(Transaction.TransactionType.TOP_UP);
        transaction.setTransactionStatus(Transaction.TransactionStatus.COMPLETED);
        transaction.setTransactionRef("TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());
        transaction.setDescription("Money added via " + paymentMethod);
        transaction.setPaymentMethod(paymentMethod);
        transaction.setBalanceBefore(balanceBefore);
        transaction.setBalanceAfter(balanceAfter);
        transaction.setCompletedAt(LocalDateTime.now());

        // Update wallet balance
        wallet.setBalance(balanceAfter);
        walletRepository.save(wallet);

        // Save transaction
        Transaction savedTxn = transactionRepository.save(transaction);

        log.info("Money added to wallet {}: {}", walletNumber, amount);

        return java.util.Map.of(
            "transactionId", savedTxn.getId(),
            "transactionReference", savedTxn.getTransactionRef(),
            "amount", amount,
            "newBalance", wallet.getBalance(),
            "status", "COMPLETED"
        );
    }

    // Withdraw Money by Wallet Number (Convenience method)
    public Object withdrawMoneyByWalletNumber(String walletNumber, java.util.Map<String, Object> request) {
        Wallet wallet = getWalletByNumber(walletNumber);

        // Extract parameters from request
        BigDecimal amount = new BigDecimal(request.get("amount").toString());
        String bankAccount = (String) request.getOrDefault("bankAccount", "XXXXX1234");

        // Check balance
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new WalletException("Insufficient balance");
        }

        // Store balance before
        BigDecimal balanceBefore = wallet.getBalance();
        BigDecimal balanceAfter = balanceBefore.subtract(amount);

        // Create transaction
        Transaction transaction = new Transaction();
        transaction.setWallet(wallet);
        transaction.setAmount(amount);
        transaction.setTransactionType(Transaction.TransactionType.WITHDRAWAL);
        transaction.setTransactionStatus(Transaction.TransactionStatus.COMPLETED);
        transaction.setTransactionRef("TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());
        transaction.setDescription("Withdrawal to bank account " + bankAccount);
        transaction.setBalanceBefore(balanceBefore);
        transaction.setBalanceAfter(balanceAfter);
        transaction.setCompletedAt(LocalDateTime.now());

        // Update wallet balance
        wallet.setBalance(balanceAfter);
        walletRepository.save(wallet);

        // Save transaction
        Transaction savedTxn = transactionRepository.save(transaction);

        log.info("Money withdrawn from wallet {}: {}", walletNumber, amount);

        return java.util.Map.of(
            "transactionId", savedTxn.getId(),
            "transactionReference", savedTxn.getTransactionRef(),
            "amount", amount,
            "newBalance", wallet.getBalance(),
            "status", "COMPLETED"
        );
    }
}
