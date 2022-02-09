package com.webster.forexproxy.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import lombok.Data;

@Data
public class Rate {
    private BigDecimal price;
    private long timestamp;

    public static Rate of(BigDecimal price, ZonedDateTime timestamp) {
        final var rate = new Rate();
        rate.setPrice(price);
        rate.setTimestamp(timestamp.toEpochSecond());
        return rate;
    }
}
