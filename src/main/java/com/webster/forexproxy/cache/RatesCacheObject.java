package com.webster.forexproxy.cache;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RatesCacheObject {
    private BigDecimal price;
    private long timestamp;
    private long expiresNanos;
}
