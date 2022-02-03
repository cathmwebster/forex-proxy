package com.webster.forexproxy.model.api;

import lombok.Getter;

public abstract class ApiResponseBase {
    @Getter
    private int status;

    public ApiResponseBase(int status) {
        this.status = status;
    }
}
