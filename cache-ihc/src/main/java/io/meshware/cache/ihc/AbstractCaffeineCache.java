/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.meshware.cache.ihc;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.github.benmanes.caffeine.cache.RemovalCause;
import io.meshware.cache.api.LocalCache;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Abstract base class for Caffeine-based caches.
 * <p>
 * Extracts common fields and methods shared by {@link AbstractCommonCache} and
 * {@link AbstractLoadingCache} to eliminate code duplication.
 * </p>
 *
 * @author Zhiguo.Chen
 */
@Slf4j
@Data
@Accessors(chain = true)
public abstract class AbstractCaffeineCache<K, V> implements LocalCache<K, V> {

    /**
     * Cache name
     */
    protected String name;

    /**
     * Cache auto-refresh duration
     */
    protected int refreshDuration = -1;

    /**
     * Cache refresh time unit
     */
    protected TimeUnit refreshTimeUnit = TimeUnit.MINUTES;

    /**
     * Cache expiration duration after write
     */
    protected int expireDurationAfterWrite = -1;

    /**
     * Cache expiration duration after access
     */
    protected int expireDurationAfterAccess = -1;

    /**
     * Custom data expiry policy
     */
    protected Supplier<Expiry<K, V>> expirySupplier;

    /**
     * Cache expiration time unit
     */
    protected TimeUnit expireTimeUnit = TimeUnit.HOURS;

    /**
     * Cache max size
     */
    protected long maxSize = 10000;

    @Override
    public String getName() {
        return name != null ? name : LocalCache.super.getName();
    }

    public void buildCache() throws Exception {
        initConfig();
        init();
    }

    /**
     * Init cache config
     */
    public abstract void initConfig();

    /**
     * Init cache instance
     */
    protected abstract void init();

    /**
     * Build a pre-configured Caffeine builder with common settings.
     *
     * @return configured Caffeine builder
     */
    @SuppressWarnings("unchecked")
    protected Caffeine<K, V> buildCaffeine() {
        Caffeine<K, V> cacheBuilder = (Caffeine<K, V>) Caffeine.newBuilder()
                .maximumSize(maxSize)
                .removalListener((key, value, removalCause) ->
                        whenRemove((K) key, (V) value, removalCause));
        if (refreshDuration > 0) {
            cacheBuilder = cacheBuilder.refreshAfterWrite(refreshDuration, refreshTimeUnit);
        }
        if (expireDurationAfterWrite > 0) {
            cacheBuilder = cacheBuilder.expireAfterWrite(expireDurationAfterWrite, expireTimeUnit);
        }
        if (expireDurationAfterAccess > 0) {
            cacheBuilder = cacheBuilder.expireAfterAccess(expireDurationAfterAccess, expireTimeUnit);
        }
        if (null != expirySupplier) {
            cacheBuilder = cacheBuilder.expireAfter(expirySupplier.get());
        }
        return cacheBuilder;
    }

    /**
     * Get the underlying Caffeine Cache instance.
     * Subclasses must implement lazy initialization with double-checked locking.
     *
     * @return Cache instance
     */
    protected abstract Cache<K, V> getCaffeineCache();

    @Override
    public abstract V getValue(K key);

    @Override
    public void putValue(K key, V value) {
        getCaffeineCache().put(key, value);
    }

    @Override
    public V getValueOrDefault(K key, V defaultValue) {
        V result = getValue(key);
        return result == null ? defaultValue : result;
    }

    @Override
    public abstract V getValueOrSupplier(K key, Supplier<V> defaultValueSupplier);

    @Override
    public void removeValue(K key) {
        if (log.isInfoEnabled()) {
            log.info("The key[{}] of the current cache has been discarded! Cache class:{}", key, this.getClass().getSimpleName());
        }
        getCaffeineCache().invalidate(key);
    }

    @Override
    public void removeAll() {
        if (log.isInfoEnabled()) {
            log.info("The cache will be discarded! Cache class:{}", this.getClass().getSimpleName());
        }
        getCaffeineCache().invalidateAll();
    }

    @Override
    public void cleanUp() {
        getCaffeineCache().cleanUp();
    }

    @Override
    public Set<K> getKeys() {
        return getCaffeineCache().asMap().keySet();
    }

    @Override
    public Collection<V> getValues() {
        return getCaffeineCache().asMap().values();
    }

    @Override
    public long getSize() {
        return getCaffeineCache().estimatedSize();
    }

    @Override
    public boolean containsKey(K key) {
        return getCaffeineCache().asMap().containsKey(key);
    }

    /**
     * Cache removal listener
     *
     * @param key          key
     * @param value        value
     * @param removalCause remove cause
     */
    public void whenRemove(K key, V value, RemovalCause removalCause) {
        if (log.isDebugEnabled()) {
            log.debug("[RemoveCallback]Remove cache key:{}, value:{}, cause:{}, cacheName={}",
                    key, value, removalCause, getName());
        }
    }
}
