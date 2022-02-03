package com.webster.forexproxy.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CurrencyNotSupportedException extends Exception {
    public CurrencyNotSupportedException(String message) {
        super(message);
    }
}
