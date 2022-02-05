package com.webster.forexproxy.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.webster.forexproxy.cache.RatesCacheObject;
import com.webster.forexproxy.cache.RatesCacheService;
import com.webster.forexproxy.exception.DataNotAvailableException;
import com.webster.forexproxy.exception.InvalidRatesRequestException;
import com.webster.forexproxy.model.Currency;
import com.webster.forexproxy.model.Rate;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RatesService {

    private final RatesCacheService ratesCacheService;

    /**
     * Returns the currency rate of from -> to currencies
     *
     * @param from currency
     * @param to currency
     * @return the rate of currencies from -> to
     */
    public Rate getRates(Currency from, Currency to) throws Exception {
        if (from == to) {
            throw new InvalidRatesRequestException();
        }

        if (from == Currency.JPY) {
            final var cachedRate = getFromCacheOrThrowException(to);
            return Rate.of(cachedRate.getPrice());
        } else if (to == Currency.JPY) {
            final var cachedFromRate = getFromCacheOrThrowException(from);
            return Rate.of(BigDecimal.ONE.divide(cachedFromRate.getPrice(), MathContext.DECIMAL64));
        } else {
            final var cachedFromRate = getFromCacheOrThrowException(from);
            final var cachedToRate = getFromCacheOrThrowException(to);
            final var inversed = BigDecimal.ONE.divide(cachedFromRate.getPrice(), MathContext.DECIMAL64);
            final var converted = inversed.multiply(cachedToRate.getPrice(), MathContext.DECIMAL64);
            return Rate.of(converted);
        }
    }

    /**
     * Retrieves value of the given key, throws exception if the value is null
     * @param key of cache
     * @return RatesCacheObject
     * @throws DataNotAvailableException
     */
    private RatesCacheObject getFromCacheOrThrowException(Currency key) throws DataNotAvailableException {
        return Optional.ofNullable(ratesCacheService.get(key))
                .orElseThrow(DataNotAvailableException::new);
    }
}
