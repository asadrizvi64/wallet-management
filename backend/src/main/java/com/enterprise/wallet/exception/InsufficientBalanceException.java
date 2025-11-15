package com.enterprise.wallet.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when wallet has insufficient balance for a transaction
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InsufficientBalanceException extends WalletException {

    public InsufficientBalanceException(String message) {
        super(message);
    }

    public InsufficientBalanceException(String walletNumber, String requiredAmount) {
        super(String.format("Wallet %s has insufficient balance. Required: %s", walletNumber, requiredAmount));
    }
}
