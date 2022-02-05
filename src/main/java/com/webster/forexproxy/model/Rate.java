package com.webster.forexproxy.model;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class Rate {
    private BigDecimal price;
    private long timestamp;

    public static Rate of(BigDecimal price, long timestamp) {
        final var rate = new Rate();
        rate.setPrice(price);
        rate.setTimestamp(timestamp);
        return rate;
    }
}
