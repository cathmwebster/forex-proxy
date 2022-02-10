package com.webster.forexproxy.cache;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
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

    @Value("${cache.refresh-period-ms}")
    private String refreshPeriod;

    private final CacheConfig cacheConfig;
    private final OneFrameRateApiClient oneFrameRateApiClient;
    private final Cache<Currency, RatesCacheObject> ratesCache = Caffeine.newBuilder()
                                                                         .expireAfter(new CacheExpiry())
                                                                         .build();

    @Scheduled(initialDelayString = "${cache.initial-delay-ms}", fixedRateString = "${cache.refresh-period-ms}")
    public void refreshCache() {
        final var now = ZonedDateTime.now(ZoneOffset.UTC);
        final var expiredTimestamp = now
                                                  .minusNanos(cacheConfig.getDefaultExpireNanos());
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

        int count = 0;
        for (final var res : response) {
            // ignore responses that couldn't parse the timestamp or has timestamp older than 5 minutes
            if (res.getTimestamp() == null || res.getTimestamp().isBefore(expiredTimestamp)) {
                continue;
            }

            // get the expire nanos by the difference between now and timestamp + 5 minutes
            final var expiresAt = res.getTimestamp()
                                     .plusNanos(cacheConfig.getDefaultExpireNanos());
            final var expireNanos = ChronoUnit.NANOS.between(now, expiresAt);
            final var obj = new RatesCacheObject(res.getPrice(),
                                                 res.getTimestamp(),
                                                 expireNanos);
            put(Currency.valueOf(res.getTo()), obj);
            count++;
        }
        
        log.info("Count of refreshed cache keys / total in response: {} / {}", count, response.size());
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
