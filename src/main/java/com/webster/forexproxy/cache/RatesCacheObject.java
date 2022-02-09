package com.webster.forexproxy.cache;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RatesCacheObject {
    private BigDecimal price;
    private ZonedDateTime timestamp;
    private long expiresNanos;
}
