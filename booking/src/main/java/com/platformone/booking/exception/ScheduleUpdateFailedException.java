package com.platformone.booking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ScheduleUpdateFailedException extends RuntimeException {
    public ScheduleUpdateFailedException(String message) {
        super(message);
    }
}