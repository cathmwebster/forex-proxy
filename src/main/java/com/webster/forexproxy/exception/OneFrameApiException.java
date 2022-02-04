package com.webster.forexproxy.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class OneFrameApiException extends Exception {
    public OneFrameApiException(String message) {
        super(message);
    }
}
