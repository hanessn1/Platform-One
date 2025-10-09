package com.platformone.payment.exception;

public class DuplicateWalletForUserException extends RuntimeException{
    public DuplicateWalletForUserException(String message) {
        super(message);
    }

    public DuplicateWalletForUserException() {
        super("A wallet for this user already exists");
    }
}