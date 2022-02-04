package com.webster.forexproxy.cache;

import java.math.BigDecimal;

import lombok.Getter;

@Getter
public class RatesCacheObject {
    private BigDecimal price;
    private long timestamp;
}
