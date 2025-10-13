package com.platformone.schedule.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class TrainServiceUnavailableException extends RuntimeException{
    public TrainServiceUnavailableException(String message){
        super(message);
    }
}