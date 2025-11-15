package com.enterprise.wallet.controller;

import com.enterprise.wallet.dto.*;
import com.enterprise.wallet.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "Transaction Management", description = "APIs for transaction operations")
public class TransactionController {
    
    private final TransactionService transactionService;
    
    // API 6: Add Money (Top-up)
    @PostMapping("/add-money")
    @Operation(summary = "Add money to wallet", description = "Adds money to a wallet from external source")
    public ResponseEntity<ApiResponse<TransactionResponse>> addMoney(
            @Valid @RequestBody AddMoneyRequest request) {
        
        TransactionResponse transaction = transactionService.addMoney(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Money added successfully", transaction));
    }
    
    // API 7: Withdraw Money
    @PostMapping("/withdraw")
    @Operation(summary = "Withdraw money", description = "Withdraws money from wallet to external account")
    public ResponseEntity<ApiResponse<TransactionResponse>> withdrawMoney(
            @Valid @RequestBody WithdrawMoneyRequest request) {
        
        TransactionResponse transaction = transactionService.withdrawMoney(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Money withdrawn successfully", transaction));
    }
    
    // API 8: Transfer Money
    @PostMapping("/transfer")
    @Operation(summary = "Transfer money", description = "Transfers money between two wallets")
    public ResponseEntity<ApiResponse<TransactionResponse>> transferMoney(
            @Valid @RequestBody TransferMoneyRequest request) {
        
        TransactionResponse transaction = transactionService.transferMoney(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Money transferred successfully", transaction));
    }
    
    // API 9: Get Transaction Details
    @GetMapping("/{transactionRef}")
    @Operation(summary = "Get transaction details", description = "Retrieves details of a specific transaction")
    public ResponseEntity<ApiResponse<TransactionResponse>> getTransactionDetails(
            @PathVariable String transactionRef) {
        
        TransactionResponse transaction = transactionService.getTransactionDetails(transactionRef);
        return ResponseEntity.ok(ApiResponse.success("Transaction details retrieved", transaction));
    }
    
    // API 10: Get Transaction History
    @GetMapping("/wallet/{walletId}/history")
    @Operation(summary = "Get transaction history", description = "Retrieves all transactions for a wallet")
    public ResponseEntity<ApiResponse<TransactionHistoryResponse>> getTransactionHistory(
            @PathVariable Long walletId) {
        
        TransactionHistoryResponse history = transactionService.getTransactionHistory(walletId);
        return ResponseEntity.ok(ApiResponse.success("Transaction history retrieved", history));
    }
    
    // API 11: Cancel Transaction
    @PutMapping("/{transactionRef}/cancel")
    @Operation(summary = "Cancel transaction", description = "Cancels a pending transaction")
    public ResponseEntity<ApiResponse<TransactionResponse>> cancelTransaction(
            @PathVariable String transactionRef) {
        
        TransactionResponse transaction = transactionService.cancelTransaction(transactionRef);
        return ResponseEntity.ok(ApiResponse.success("Transaction cancelled", transaction));
    }
    
    // API 12: Refund Transaction
    @PostMapping("/refund")
    @Operation(summary = "Refund transaction", description = "Processes a refund for a completed transaction")
    public ResponseEntity<ApiResponse<TransactionResponse>> refundTransaction(
            @Valid @RequestBody RefundTransactionRequest request) {
        
        TransactionResponse transaction = transactionService.refundTransaction(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Refund processed successfully", transaction));
    }
    
    // API 13: Process Payment
    @PostMapping("/payment")
    @Operation(summary = "Process payment", description = "Processes a payment using wallet balance")
    public ResponseEntity<ApiResponse<TransactionResponse>> processPayment(
            @Valid @RequestBody ProcessPaymentRequest request) {
        
        TransactionResponse transaction = transactionService.processPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Payment processed successfully", transaction));
    }
}
