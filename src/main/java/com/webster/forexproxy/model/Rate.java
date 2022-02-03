package com.webster.forexproxy.model;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class Rate {
    private BigDecimal price;

    public static Rate of(BigDecimal price) {
        final var rate = new Rate();
        rate.setPrice(price);
        return rate;
    }
}
