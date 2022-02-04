package com.webster.forexproxy.cache;

import com.github.benmanes.caffeine.cache.Expiry;
import com.webster.forexproxy.model.Currency;

class CacheExpiry implements Expiry<Currency, RatesCacheObject> {

    @Override
    public long expireAfterCreate(Currency key, RatesCacheObject value, long currentTime) {
        return value.getExpiresNanos();
    }

    @Override
    public long expireAfterUpdate(Currency key, RatesCacheObject value, long currentTime,
                                  long currentDuration) {
        return value.getExpiresNanos();
    }

    @Override
    public long expireAfterRead(Currency key, RatesCacheObject value, long currentTime,
                                long currentDuration) {
        return currentDuration;
    }
}