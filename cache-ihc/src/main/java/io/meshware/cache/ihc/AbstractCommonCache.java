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

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Abstract Common Cache
 *
 * @author Zhiguo.Chen
 */
@Slf4j
@Data
@Accessors(chain = true)
public abstract class AbstractCommonCache<K, V> implements LocalCache<K, V> {

    /**
     * 缓存自动刷新周期
     */
    protected int refreshDuration = -1;

    /**
     * 缓存刷新周期时间格式
     */
    protected TimeUnit refreshTimeUnit = TimeUnit.MINUTES;

    /**
     * 缓存过期时间（创建后）
     */
    protected int expireDurationAfterWrite = -1;

    /**
     * 缓存过期时间（访问后）
     */
    protected int expireDurationAfterAccess = -1;

    /**
     * 自定义数据过期策略
     */
    protected Supplier<Expiry<K, V>> expirySupplier;

    /**
     * 缓存刷新周期时间格式
     */
    protected TimeUnit expireTimeUnit = TimeUnit.HOURS;

    /**
     * Cache max size
     */
    protected long maxSize = 10000;

    /**
     * Cache instance
     */
    private volatile Cache<K, V> cache = null;

    @PostConstruct
    public void afterPropertiesSet() throws Exception {
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
    protected synchronized void init() {
        Caffeine<K, V> cacheBuilder = Caffeine.newBuilder().maximumSize(maxSize).removalListener(
                (key, value, removalCause) -> whenRemove(key, value, removalCause)
        );
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
        cache = cacheBuilder.build();
        //Init cache
        initCache(cache);
    }

    /**
     * Init cache
     *
     * @param cache cache
     */
    public abstract void initCache(Cache<K, V> cache);

    @Override
    public V getValue(K key) {
        return getCache().getIfPresent(key);
    }

    @Override
    public void putValue(K key, V value) {
        getCache().put(key, value);
    }

    @Override
    public V getValueOrDefault(K key, V defaultValue) {
        V result = getValue(key);
        return result == null ? defaultValue : result;
    }

    /**
     * Get value and return default value if not exist
     *
     * @param key                  key
     * @param defaultValueSupplier default value supplier
     * @return V
     */
    @Override
    public V getValueOrSupplier(K key, Supplier<V> defaultValueSupplier) {
        V result = getValue(key);
        if (result == null) {
            synchronized (this) {
                result = getValue(key);
                if (result == null) {
                    result = defaultValueSupplier.get();
                    putValue(key, result);
                }
            }
        }
        return result;
    }

    @Override
    public void removeValue(K key) {
        if (log.isInfoEnabled()) {
            log.info("The key[{}] of the current cache has been discarded! Cache class:{}", key, this.getClass().getSimpleName());
        }
        this.getCache().invalidate(key);
    }

    @Override
    public void removeAll() {
        if (log.isInfoEnabled()) {
            log.info("The cache will be discarded! Cache class:{}", this.getClass().getSimpleName());
        }
        this.getCache().invalidateAll();
    }

    @Override
    public void cleanUp() {
        this.getCache().cleanUp();
    }

    @Override
    public Set<K> getKeys() {
        return this.getCache().asMap().keySet();
    }

    @Override
    public Collection<V> getValues() {
        return this.getCache().asMap().values();
    }

    @Override
    public long getSize() {
        return this.getCache().estimatedSize();
    }

    @Override
    public boolean containsKey(K key) {
        return this.getCache().asMap().containsKey(key);
    }

    /**
     * Create cache instance
     *
     * @return Cache
     */
    private Cache<K, V> getCache() {
        if (cache == null) {
            synchronized (this) {
                if (cache == null) {
                    init();
                }
            }
        }
        return cache;
    }

    /**
     * 缓存移除监听器
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
