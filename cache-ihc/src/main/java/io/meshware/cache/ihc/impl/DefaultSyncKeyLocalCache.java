package io.meshware.cache.ihc.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.RemovalCause;
import io.meshware.cache.ihc.AbstractCommonCache;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * Sync key local cache
 *
 * @author Zhiguo.Chen
 */
@Slf4j
public class DefaultSyncKeyLocalCache extends AbstractCommonCache<String, String> {

    @Override
    public String getName() {
        return "defaultSyncKeyLocalCache";
    }

    @Override
    public void initConfig() {
        this.setMaxSize(10000);
        this.setExpireTimeUnit(TimeUnit.HOURS);
        this.setExpireDurationAfterWrite(8);
    }

    @Override
    public void initCache(Cache<String, String> cache) {

    }

    @Override
    public void whenRemove(String key, String value, RemovalCause removalCause) {
        if (removalCause == RemovalCause.SIZE || removalCause == RemovalCause.COLLECTED) {
            log.error("缓存失效！失效原因：{}, 缓存：{}", removalCause.toString(), getName());
        }
        super.whenRemove(key, value, removalCause);
    }
}