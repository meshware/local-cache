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
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

/**
 * Abstract Common Cache (manual put/get, no auto-loading)
 *
 * @author Zhiguo.Chen
 */
@Slf4j
@Data
@Accessors(chain = true)
public abstract class AbstractCommonCache<K, V> extends AbstractCaffeineCache<K, V> {

    /**
     * Cache instance
     */
    private volatile Cache<K, V> cache = null;

    /**
     * Init cache instance
     */
    @Override
    protected synchronized void init() {
        cache = buildCaffeine().build();
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
        return getCaffeineCache().getIfPresent(key);
    }

    /**
     * Get value and return default value if not exist.
     * Uses Caffeine's built-in atomic compute-if-absent, which provides
     * per-key locking instead of the previous coarse-grained synchronized(this).
     *
     * @param key                  key
     * @param defaultValueSupplier default value supplier
     * @return V
     */
    @Override
    public V getValueOrSupplier(K key, Supplier<V> defaultValueSupplier) {
        return getCaffeineCache().get(key, k -> defaultValueSupplier.get());
    }

    /**
     * Get or create cache instance (lazy init with double-checked locking)
     *
     * @return Cache
     */
    @Override
    protected Cache<K, V> getCaffeineCache() {
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
