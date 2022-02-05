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
            return Rate.of(cachedRate.getPrice(), cachedRate.getTimestamp());
        } else if (to == Currency.JPY) {
            final var cachedRate = getFromCacheOrThrowException(from);
            return Rate.of(getInverse(cachedRate.getPrice()), cachedRate.getTimestamp());
        } else {
            final var cachedFromRate = getFromCacheOrThrowException(from);
            final var cachedToRate = getFromCacheOrThrowException(to);
            return Rate.of(convertByInverseOnFrom(cachedFromRate.getPrice(), cachedToRate.getPrice()),
                           Math.max(cachedFromRate.getTimestamp(), cachedToRate.getTimestamp()));
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

    private BigDecimal getInverse(BigDecimal price) {
        return BigDecimal.ONE.divide(price, MathContext.DECIMAL64);
    }

    private BigDecimal convertByInverseOnFrom(BigDecimal fromPrice, BigDecimal toPrice) {
        return getInverse(fromPrice)
                .multiply(toPrice, MathContext.DECIMAL64);
    }
}
