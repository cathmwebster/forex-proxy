package com.webster.forexproxy.cache;

import java.util.concurrent.TimeUnit;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties("cache")
@Data
public class CacheConfig {
    private long defaultExpireSeconds;
    private long initialDelaySeconds;
    private long refreshPeriodSeconds;

    public long getDefaultExpireNanos() {
        return TimeUnit.SECONDS.toNanos(defaultExpireSeconds);
    }
}
