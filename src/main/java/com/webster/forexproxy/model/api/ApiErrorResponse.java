package com.webster.forexproxy.model.api;

import lombok.Getter;
import lombok.Value;

@Value
public class ApiErrorResponse extends ApiResponseBase {

    private Error error;

    public ApiErrorResponse(int status, String message) {
        super(status);
        this.error = new Error(message);
    }

    @Getter
    public static class Error {
        private String message;
        public Error(String message) {
            this.message = message;
        }
    }
}
