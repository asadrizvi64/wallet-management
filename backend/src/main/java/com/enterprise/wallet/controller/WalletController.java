package com.enterprise.wallet.controller;

import com.enterprise.wallet.dto.ApiResponse;
import com.enterprise.wallet.dto.TransactionDTOs.*;
import com.enterprise.wallet.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
@Tag(name = "Wallet Management", description = "APIs for wallet operations")
public class WalletController {

    private final WalletService walletService;
    private final com.enterprise.wallet.service.TransactionService transactionService;
    
    // API 1: Create Wallet
    @PostMapping("/create")
    @Operation(summary = "Create a new wallet", description = "Creates a new wallet for a user")
    public ResponseEntity<ApiResponse<WalletResponse>> createWallet(
            @RequestParam Long userId,
            @Valid @RequestBody CreateWalletRequest request) {
        
        WalletResponse wallet = walletService.createWallet(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Wallet created successfully", wallet));
    }
    
    // API 2: Get Wallet Details
    @GetMapping("/{walletNumber}")
    @Operation(summary = "Get wallet details", description = "Retrieves details of a specific wallet by wallet number")
    public ResponseEntity<ApiResponse<WalletResponse>> getWalletDetails(
            @PathVariable String walletNumber) {

        // Get wallet by wallet number first
        com.enterprise.wallet.entity.Wallet wallet = walletService.getWalletByNumber(walletNumber);
        WalletResponse walletResponse = walletService.getWalletDetails(wallet.getId());
        return ResponseEntity.ok(ApiResponse.success("Wallet details retrieved", walletResponse));
    }
    
    // API 3: Get Wallet Balance
    @GetMapping("/{walletNumber}/balance")
    @Operation(summary = "Get wallet balance", description = "Retrieves current balance of a wallet by wallet number")
    public ResponseEntity<ApiResponse<WalletBalanceResponse>> getWalletBalance(
            @PathVariable String walletNumber) {

        // Get wallet by wallet number first
        com.enterprise.wallet.entity.Wallet wallet = walletService.getWalletByNumber(walletNumber);
        WalletBalanceResponse balance = walletService.getWalletBalance(wallet.getId());
        return ResponseEntity.ok(ApiResponse.success("Balance retrieved", balance));
    }
    
    // API 4: Update Wallet Status
    @PutMapping("/{walletNumber}/status")
    @Operation(summary = "Update wallet status", description = "Changes the status of a wallet (ACTIVE, FROZEN, BLOCKED) by wallet number")
    public ResponseEntity<ApiResponse<WalletResponse>> updateWalletStatus(
            @PathVariable String walletNumber,
            @Valid @RequestBody UpdateWalletStatusRequest request) {

        // Get wallet by wallet number first
        com.enterprise.wallet.entity.Wallet wallet = walletService.getWalletByNumber(walletNumber);
        WalletResponse walletResponse = walletService.updateWalletStatus(wallet.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Wallet status updated", walletResponse));
    }
    
    // API 5: Get User Wallets
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all wallets of a user", description = "Retrieves all wallets belonging to a user")
    public ResponseEntity<ApiResponse<List<WalletResponse>>> getUserWallets(
            @PathVariable Long userId) {

        List<WalletResponse> wallets = walletService.getUserWallets(userId);
        return ResponseEntity.ok(ApiResponse.success("User wallets retrieved", wallets));
    }

    // API 6: Add Money to Wallet (by wallet number)
    @PostMapping("/{walletNumber}/add-money")
    @Operation(summary = "Add money to wallet", description = "Adds money to a wallet using wallet number")
    public ResponseEntity<ApiResponse<TransactionResponse>> addMoneyToWallet(
            @PathVariable String walletNumber,
            @Valid @RequestBody WalletAddMoneyRequest request) {

        // Get wallet by wallet number
        com.enterprise.wallet.entity.Wallet wallet = walletService.getWalletByNumber(walletNumber);

        // Convert to AddMoneyRequest for TransactionService
        AddMoneyRequest addMoneyRequest = new AddMoneyRequest();
        addMoneyRequest.setWalletId(wallet.getId());
        addMoneyRequest.setAmount(request.getAmount());
        addMoneyRequest.setPaymentMethod(request.getPaymentMethod());
        addMoneyRequest.setDescription(request.getPaymentGatewayRef() != null
                ? "Payment Gateway Ref: " + request.getPaymentGatewayRef()
                : "Add money via wallet number");

        TransactionResponse transaction = transactionService.addMoney(addMoneyRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Money added successfully", transaction));
    }

    // API 7: Withdraw Money from Wallet (by wallet number)
    @PostMapping("/{walletNumber}/withdraw")
    @Operation(summary = "Withdraw money from wallet", description = "Withdraws money from a wallet using wallet number")
    public ResponseEntity<ApiResponse<TransactionResponse>> withdrawMoneyFromWallet(
            @PathVariable String walletNumber,
            @Valid @RequestBody WalletWithdrawMoneyRequest request) {

        // Get wallet by wallet number
        com.enterprise.wallet.entity.Wallet wallet = walletService.getWalletByNumber(walletNumber);

        // Convert to WithdrawMoneyRequest for TransactionService
        WithdrawMoneyRequest withdrawRequest = new WithdrawMoneyRequest();
        withdrawRequest.setWalletId(wallet.getId());
        withdrawRequest.setAmount(request.getAmount());
        withdrawRequest.setPaymentMethod(request.getPaymentMethod());
        withdrawRequest.setDescription(request.getBankAccountRef() != null
                ? "Bank Account Ref: " + request.getBankAccountRef()
                : "Withdraw money via wallet number");

        TransactionResponse transaction = transactionService.withdrawMoney(withdrawRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Money withdrawn successfully", transaction));
    }
}
