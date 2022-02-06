package com.webster.forexproxy.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidRatesRequestException extends Exception {
    public InvalidRatesRequestException() {
        super("The requested currency from and to cannot be the same");
    }
}
