package com.enterprise.wallet.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a transaction operation fails
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TransactionException extends RuntimeException {

    public TransactionException(String message) {
        super(message);
    }

    public TransactionException(String message, Throwable cause) {
        super(message, cause);
    }
}
