package com.github.mrchcat.accounts.exceptions;

public class NotEnoughMoney extends RuntimeException {
    public NotEnoughMoney(String message) {
        super(message);
    }
}
