package com.webster.forexproxy.service;

import java.math.BigDecimal;
import java.math.MathContext;

import org.springframework.stereotype.Service;

import com.webster.forexproxy.cache.RatesCacheService;
import com.webster.forexproxy.exception.InvalidRatesRequestException;
import com.webster.forexproxy.model.Currency;
import com.webster.forexproxy.model.Rate;

@Service
public class RatesService {

    private final RatesCacheService ratesCacheService;

    public RatesService(RatesCacheService ratesCacheService) {
        this.ratesCacheService = ratesCacheService;
    }

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

        // TODO null check
        if (from == Currency.JPY) {
            return Rate.of(ratesCacheService.get(to).getPrice());
        } else if (to == Currency.JPY) {
            return Rate.of(BigDecimal.ONE.divide(ratesCacheService.get(from).getPrice(), MathContext.DECIMAL64));
        } else {
            var inversed = BigDecimal.ONE.divide(ratesCacheService.get(from).getPrice(), MathContext.DECIMAL64);
            return Rate.of(inversed.multiply(ratesCacheService.get(to).getPrice(), MathContext.DECIMAL64));
        }
    }
}
