package com.enterprise.wallet.exception;

/**
 * Base exception for all wallet-related operations
 */
public class WalletException extends RuntimeException {

    public WalletException(String message) {
        super(message);
    }

    public WalletException(String message, Throwable cause) {
        super(message, cause);
    }
}
