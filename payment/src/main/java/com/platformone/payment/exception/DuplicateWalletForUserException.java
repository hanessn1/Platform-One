package com.platformone.payment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateWalletForUserException extends RuntimeException{
    public DuplicateWalletForUserException(String message) {
        super(message);
    }

    public DuplicateWalletForUserException() {
        super("A wallet for this user already exists");
    }
}