package com.platformone.booking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class BookingAlreadyCancelledException extends RuntimeException{
    public BookingAlreadyCancelledException(String message) {
        super(message);
    }
}