package com.enterprise.wallet.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a transaction limit is exceeded
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class LimitExceededException extends TransactionException {

    public LimitExceededException(String message) {
        super(message);
    }

    public LimitExceededException(String limitType, String amount) {
        super(String.format("%s limit exceeded. Attempted amount: %s", limitType, amount));
    }
}
