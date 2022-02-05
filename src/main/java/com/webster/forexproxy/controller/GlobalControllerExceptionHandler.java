package com.webster.forexproxy.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.webster.forexproxy.exception.CurrencyNotSupportedException;
import com.webster.forexproxy.exception.DataNotAvailableException;
import com.webster.forexproxy.exception.InvalidRatesRequestException;
import com.webster.forexproxy.model.api.ApiErrorResponse;

@RestControllerAdvice
public class GlobalControllerExceptionHandler {

    @ExceptionHandler({ CurrencyNotSupportedException.class, InvalidRatesRequestException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse badRequestException(Exception ex) {
        return new ApiErrorResponse(400, ex.getMessage());
    }

    @ExceptionHandler(DataNotAvailableException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorResponse dataNotAvailableException(DataNotAvailableException ex) {
        return new ApiErrorResponse(500, ex.getMessage());
    }
}
