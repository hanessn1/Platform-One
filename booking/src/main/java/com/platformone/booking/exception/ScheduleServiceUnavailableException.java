package com.platformone.booking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class ScheduleServiceUnavailableException extends RuntimeException{
    public ScheduleServiceUnavailableException(String message) {
        super(message);
    }
}