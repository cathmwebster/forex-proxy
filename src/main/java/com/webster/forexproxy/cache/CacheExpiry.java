package com.webster.forexproxy.cache;

import com.github.benmanes.caffeine.cache.Expiry;

class CacheExpiry implements Expiry<String, RatesCacheObject> {

    @Override
    public long expireAfterCreate(String key, RatesCacheObject value, long currentTime) {
        return value.getExpireAt();
    }

    @Override
    public long expireAfterUpdate(String key, RatesCacheObject value, long currentTime,
                                  long currentDuration) {
        return value.getExpireAt();
    }

    @Override
    public long expireAfterRead(String key, RatesCacheObject value, long currentTime,
                                long currentDuration) {
        return currentDuration;
    }
}