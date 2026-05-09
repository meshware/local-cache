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
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

/**
 * Abstract Loading Cache (auto-loading via CacheLoader)
 *
 * @author Zhiguo.Chen
 */
@Slf4j
@Data
@Accessors(chain = true)
public abstract class AbstractLoadingCache<K, V> extends AbstractCaffeineCache<K, V> {

    /**
     * Cache instance
     */
    private volatile LoadingCache<K, V> cache = null;

    /**
     * Init cache instance
     */
    @Override
    protected synchronized void init() {
        cache = buildCaffeine().build(new CacheLoader<K, V>() {
            @Override
            public V load(K key) throws Exception {
                if (log.isInfoEnabled()) {
                    log.info("Loading data, cache name={}, current key={}, current cache estimatedSize={}, max size={}",
                            getName(), key, cache.estimatedSize(), maxSize);
                }
                return getValueWhenExpired(key);
            }

            @Override
            public V reload(final K key, V oldValue) throws Exception {
                if (log.isInfoEnabled()) {
                    log.info("Refresh data in cache, key={}, value={}", key, oldValue);
                }
                return getValueWhenRefresh(key, oldValue);
            }
        });
        //Init cache
        initCache(cache);
    }

    /**
     * Init cache
     *
     * @param cache cache
     */
    public abstract void initCache(LoadingCache<K, V> cache);

    /**
     * Get data when old data expired
     *
     * @param key key
     * @return V Not null
     * @throws Exception exception
     */
    protected abstract V getValueWhenExpired(K key) throws Exception;

    /**
     * Get data when the refresh event occurs
     *
     * @param key      Key
     * @param oldValue Old value
     * @return V Not null
     * @throws Exception exception
     */
    protected V getValueWhenRefresh(K key, V oldValue) throws Exception {
        return oldValue;
    }

    /**
     * Get value by key (triggers CacheLoader if absent)
     *
     * @param key key
     * @return V
     */
    @Override
    public V getValue(K key) {
        return getLoadingCache().get(key);
    }

    /**
     * Get value and return supplier's value if the loaded value is null.
     * Since LoadingCache.get() triggers the CacheLoader, the supplier is
     * only used as a fallback when loading fails or returns null.
     *
     * @param key                  key
     * @param defaultValueSupplier default value supplier
     * @return V
     */
    @Override
    public V getValueOrSupplier(K key, Supplier<V> defaultValueSupplier) {
        V result = getValue(key);
        if (result == null) {
            result = defaultValueSupplier.get();
            if (result != null) {
                putValue(key, result);
            }
        }
        return result;
    }

    /**
     * Check if the data exists remotely
     *
     * @param key key
     * @return bool
     */
    public boolean remoteContains(K key) {
        throw new UnsupportedOperationException("This operation is not supported in the current cache!");
    }

    /**
     * Get or create cache instance (lazy init with double-checked locking)
     *
     * @return LoadingCache (cast to Cache for base class compatibility)
     */
    @Override
    protected Cache<K, V> getCaffeineCache() {
        return getLoadingCache();
    }

    /**
     * Get the LoadingCache instance
     *
     * @return LoadingCache
     */
    protected LoadingCache<K, V> getLoadingCache() {
        if (cache == null) {
            synchronized (this) {
                if (cache == null) {
                    init();
                }
            }
        }
        return cache;
    }
}
