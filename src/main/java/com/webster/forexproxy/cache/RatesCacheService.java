package com.webster.forexproxy.cache;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.webster.forexproxy.exception.OneFrameApiException;
import com.webster.forexproxy.model.Currency;
import com.webster.forexproxy.oneframe.client.OneFrameRateApiClient;
import com.webster.forexproxy.oneframe.model.OneFrameRateApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Caches the JPY->XXX rates from One Frame Api
 * The cache key is XXX currency
 * The executor is scheduled to refresh the cache every x seconds
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RatesCacheService {

    private final CacheConfig cacheConfig;
    private final OneFrameRateApiClient oneFrameRateApiClient;
    private final Cache<Currency, RatesCacheObject> ratesCache = Caffeine.newBuilder()
                                                                         .expireAfter(new CacheExpiry())
                                                                         .build();

    @Scheduled(initialDelayString = "${cache.initial-delay-ms}", fixedRateString = "${cache.refresh-period-ms}")
    public void refreshCache() {
        final var request = Currency.getAllValues()
                                            .stream().filter(c -> c != Currency.JPY)
                                            .map(this::generateRequestParam)
                                            .collect(Collectors.toList());
        final List<OneFrameRateApiResponse> response;
        try {
            response = oneFrameRateApiClient.getRates(request);
        } catch (OneFrameApiException e) {
            log.error("Failed to refresh cache due to exception.", e);
            return;
        }

        for (final var res : response) {
            final var obj = new RatesCacheObject(res.getPrice(),
                                                 res.getTimestamp(),
                                                 cacheConfig.getDefaultExpireNanos());
            put(Currency.valueOf(res.getTo()), obj);
        }
        
        log.info("Count of refreshed cache keys: {}", response.size());
    }

    /**
     * Gets key or return null if not present
     * @param key
     * @return
     */
    @Nullable
    public RatesCacheObject get(Currency key) {
        return ratesCache.getIfPresent(key);
    }

    /**
     * Puts key, value into cache
     * @param key
     * @return
     */
    public void put(Currency key, RatesCacheObject value) {
        ratesCache.put(key, value);
    }

    private String generateRequestParam(Currency to) {
        return Currency.JPY + to.toString();
    }

}
