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

    // API 2: Get Wallet Details by ID
    @GetMapping("/id/{walletId}")
    @Operation(summary = "Get wallet details by ID", description = "Retrieves details of a specific wallet by numeric ID")
    public ResponseEntity<ApiResponse<WalletResponse>> getWalletDetails(
            @PathVariable Long walletId) {

        WalletResponse wallet = walletService.getWalletDetails(walletId);
        return ResponseEntity.ok(ApiResponse.success("Wallet details retrieved", wallet));
    }

    // API 2b: Get Wallet Details by Wallet Number
    @GetMapping("/{walletNumber}")
    @Operation(summary = "Get wallet details by wallet number", description = "Retrieves details of a specific wallet by wallet number")
    public ResponseEntity<ApiResponse<WalletResponse>> getWalletDetailsByNumber(
            @PathVariable String walletNumber) {

        WalletResponse wallet = walletService.getWalletDetailsByNumber(walletNumber);
        return ResponseEntity.ok(ApiResponse.success("Wallet details retrieved", wallet));
    }
    
    // API 3: Get Wallet Balance
    @GetMapping("/{walletId}/balance")
    @Operation(summary = "Get wallet balance", description = "Retrieves current balance of a wallet")
    public ResponseEntity<ApiResponse<WalletBalanceResponse>> getWalletBalance(
            @PathVariable Long walletId) {
        
        WalletBalanceResponse balance = walletService.getWalletBalance(walletId);
        return ResponseEntity.ok(ApiResponse.success("Balance retrieved", balance));
    }
    
    // API 4: Update Wallet Status
    @PutMapping("/{walletId}/status")
    @Operation(summary = "Update wallet status", description = "Changes the status of a wallet (ACTIVE, FROZEN, BLOCKED)")
    public ResponseEntity<ApiResponse<WalletResponse>> updateWalletStatus(
            @PathVariable Long walletId,
            @Valid @RequestBody UpdateWalletStatusRequest request) {
        
        WalletResponse wallet = walletService.updateWalletStatus(walletId, request);
        return ResponseEntity.ok(ApiResponse.success("Wallet status updated", wallet));
    }
    
    // API 5: Get User Wallets
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all wallets of a user", description = "Retrieves all wallets belonging to a user")
    public ResponseEntity<ApiResponse<List<WalletResponse>>> getUserWallets(
            @PathVariable Long userId) {

        List<WalletResponse> wallets = walletService.getUserWallets(userId);
        return ResponseEntity.ok(ApiResponse.success("User wallets retrieved", wallets));
    }

    // Convenience API: Add Money by Wallet Number
    @PostMapping("/{walletNumber}/add-money")
    @Operation(summary = "Add money to wallet by wallet number", description = "Adds money to a wallet using wallet number")
    public ResponseEntity<ApiResponse<Object>> addMoneyByWalletNumber(
            @PathVariable String walletNumber,
            @RequestBody java.util.Map<String, Object> request) {

        Object result = walletService.addMoneyByWalletNumber(walletNumber, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Money added successfully", result));
    }

    // Convenience API: Withdraw Money by Wallet Number
    @PostMapping("/{walletNumber}/withdraw")
    @Operation(summary = "Withdraw money by wallet number", description = "Withdraws money from wallet using wallet number")
    public ResponseEntity<ApiResponse<Object>> withdrawMoneyByWalletNumber(
            @PathVariable String walletNumber,
            @RequestBody java.util.Map<String, Object> request) {

        Object result = walletService.withdrawMoneyByWalletNumber(walletNumber, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Money withdrawn successfully", result));
    }
}
