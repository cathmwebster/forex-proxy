package com.webster.forexproxy.cache;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.webster.forexproxy.exception.OneFrameApiException;
import com.webster.forexproxy.model.Currency;
import com.webster.forexproxy.oneframe.client.OneFrameRateApiClient;
import com.webster.forexproxy.oneframe.model.OneFrameRateApiResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RatesCacheService {

    // The cache will expire after 300 seconds = 5 minutes
    // since the cache will be refreshed every 90 seconds
    // there will be two more "re-try" before the cache expires
    private static final long EXPIRE_SECONDS = TimeUnit.SECONDS.toNanos(300);

    private final OneFrameRateApiClient oneFrameRateApiClient;
    private final Cache<Currency, RatesCacheObject> ratesCache;

    public RatesCacheService(OneFrameRateApiClient oneFrameRateApiClient) {
        this.ratesCache = Caffeine.newBuilder()
                                  .expireAfter(new CacheExpiry())
                                  .build();
        this.oneFrameRateApiClient = oneFrameRateApiClient;
    }

    @PostConstruct
    public void init() {
        // schedule the executor to refresh the cache every 90 seconds
        // 86400 seconds per day / 1000 req per day = 86 requests allowed per day
        // round up to 90 seconds for a cleaner period
        final ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(1);
        executor.scheduleAtFixedRate(this::refreshCache,
                                     0,
                                     90,
                                     TimeUnit.SECONDS);
    }

    private void refreshCache() {
        final var request = Currency.getAllValues()
                                            .stream().filter(c -> c != Currency.JPY)
                                            .map(this::generateRequestParam)
                                            .collect(Collectors.toList());
        final List<OneFrameRateApiResponse> response;
        try {
            response = oneFrameRateApiClient.getRates(request);
        } catch (OneFrameApiException e) {
            log.error("Failed to refresh cache due to exception. ", e);
            return;
        }

        // put each response into cache
        // we could run this async but will skip for now
        for (final var res : response) {
            // create cache object, set expire seconds to the default
            final var obj = new RatesCacheObject(res.getPrice(),
                                                 res.getTimestampInLong(),
                                                 EXPIRE_SECONDS);
            put(Currency.valueOf(res.getTo()), obj);
        }
        
        log.info("Refreshing cache...");
    }

    /**
     * Gets key or return null if not present
     * @param key
     * @return
     */
    public RatesCacheObject get(Currency key) {
        return ratesCache.getIfPresent(key);
    }

    /**
     * Puts key, value
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
