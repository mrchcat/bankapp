package com.github.mrchcat.accounts.exceptions;

public class TransactionWasCompletedAlready extends RuntimeException {
    public TransactionWasCompletedAlready(String message) {
        super(message);
    }
}
