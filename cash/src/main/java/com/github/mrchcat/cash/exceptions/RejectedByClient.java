package com.github.mrchcat.cash.exceptions;

public class RejectedByClient extends RuntimeException {
    public RejectedByClient(String message) {
        super(message);
    }
}
