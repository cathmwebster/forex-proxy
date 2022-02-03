package com.webster.forexproxy.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.webster.forexproxy.exception.InvalidRatesRequestException;
import com.webster.forexproxy.model.Currency;
import com.webster.forexproxy.model.Rate;

@Service
public class RatesService {

    /**
     * Fetches the currency rate of from -> to from One Frame and returns response
     * @param from currency
     * @param to currency
     * @return the rate of currencies from -> to
     */
    public Rate getRates(Currency from, Currency to) throws Exception {
        if (from == to) {
            throw new InvalidRatesRequestException();
        }
        // TODO main logic goes here
        return Rate.of(BigDecimal.ONE);
    }
}
