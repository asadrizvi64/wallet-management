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
@RequestMapping("/api/wallets")
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
    
    // API 2: Get Wallet Details
    @GetMapping("/{walletId}")
    @Operation(summary = "Get wallet details", description = "Retrieves details of a specific wallet")
    public ResponseEntity<ApiResponse<WalletResponse>> getWalletDetails(
            @PathVariable Long walletId) {
        
        WalletResponse wallet = walletService.getWalletDetails(walletId);
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
}
