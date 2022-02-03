package com.webster.forexproxy.model.api;

import lombok.Value;

@Value
public class ApiResponseBody<T> extends ApiResponseBase {

    private T result;

    public ApiResponseBody(int status, T result) {
        super(status);
        this.result = result;
    }

    public static <T> ApiResponseBody<T> success(T result) {
        return new ApiResponseBody(200, result);
    }
}
