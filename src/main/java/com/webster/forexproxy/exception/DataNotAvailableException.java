package com.webster.forexproxy.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DataNotAvailableException extends Exception {
    public DataNotAvailableException() {
        super("Failed to retrieve the requested currency rates");
    }
}
