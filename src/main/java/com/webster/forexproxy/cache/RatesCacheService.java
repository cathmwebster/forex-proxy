package com.webster.forexproxy.cache;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
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

    private final OneFrameRateApiClient oneFrameRateApiClient;
    private final Cache<String, RatesCacheObject> ratesCache;

    public RatesCacheService(OneFrameRateApiClient oneFrameRateApiClient) {
        this.ratesCache = Caffeine.newBuilder()
                                  .expireAfter(new CacheExpiry())
                                  .build();
        this.oneFrameRateApiClient = oneFrameRateApiClient;
    }

    // this method will be called by an executor service to refresh every x minutes
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

        log.info("Refreshing cache...");
    }

    private String generateRequestParam(Currency to) {
        return Currency.JPY + to.toString();
    }

}
