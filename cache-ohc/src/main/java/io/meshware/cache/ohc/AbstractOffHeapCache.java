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
package io.meshware.cache.ohc;

import io.meshware.cache.api.OffHeapCache;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.caffinitas.ohc.*;
import org.caffinitas.ohc.histo.EstimatedHistogram;
import org.springframework.beans.factory.InitializingBean;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Supplier;

/**
 * Abstract Off Heap Cache
 *
 * @author Zhiguo.Chen
 * @version 20210210
 */
@Slf4j
@Data
public abstract class AbstractOffHeapCache<K, V> implements OffHeapCache<K, V>, InitializingBean {

    protected volatile OHCache<K, V> cache = null;

    protected int segmentCount;
    protected int hashTableSize = 8192;
    protected long capacity;
    protected int chunkSize;
    protected float loadFactor = 0.75F;

    protected long maxEntrySize;
    protected ScheduledExecutorService executorService;
    protected boolean throwOOME;
    // protected HashAlgorithm hashAlgorighm;
    protected boolean unlocked;
    protected long defaultTTLmillis;
    protected boolean timeouts;
    protected int timeoutsSlots;
    protected int timeoutsPrecision;
    protected Ticker ticker;
    protected Eviction eviction;
    protected int frequencySketchSize;
    protected double edenSize;
    private CacheLoader<K, V> cacheLoader;

    @Override
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
    private synchronized void init() {
        if (null == cache) {
            OHCacheBuilder<K, V> builder = OHCacheBuilder.newBuilder();
            if (segmentCount > 0) {
                builder.segmentCount(segmentCount);
            }
            if (hashTableSize > 0) {
                builder.hashTableSize(hashTableSize);
            }
            if (capacity > 0) {
                builder.capacity(capacity);
            }
            if (chunkSize > 0) {
                builder.chunkSize(chunkSize);
            }
            builder.keySerializer(keySerializer());
            builder.valueSerializer(valueSerializer());
            if (loadFactor > 0) {
                builder.loadFactor(loadFactor);
            }
            if (maxEntrySize > 0) {
                builder.maxEntrySize(maxEntrySize);
            }
            if (executorService != null) {
                builder.executorService(executorService);
            } else {
                builder.executorService(Executors.newSingleThreadScheduledExecutor());
            }
            builder.throwOOME(throwOOME);
            // if (hashAlgorighm > 0) {
            //     builder.hashAlgorighm(hashAlgorighm);
            // }
            builder.unlocked(unlocked);
            if (defaultTTLmillis > 0) {
                builder.defaultTTLmillis(defaultTTLmillis);
            }
            builder.timeouts(timeouts);
            if (timeoutsSlots > 0) {
                builder.timeoutsSlots(timeoutsSlots);
            }
            if (timeoutsPrecision > 0) {
                builder.timeoutsPrecision(timeoutsPrecision);
            }
            if (ticker != null) {
                builder.ticker(ticker);
            }
            if (eviction != null) {
                builder.eviction(eviction);
            }
            if (frequencySketchSize > 0) {
                builder.frequencySketchSize(frequencySketchSize);
            }
            if (edenSize > 0) {
                builder.edenSize(edenSize);
            }
            cacheLoader = this::loadData;
            cache = builder.build();
        }
    }

    public abstract CacheSerializer<K> keySerializer();

    public abstract CacheSerializer<V> valueSerializer();

    public abstract V loadData(K key);

    @Override
    public void putValue(K key, V value) {
        put(key, value);
    }

    public boolean put(K key, V value) {
        return cache.put(key, value);
    }

    @Override
    public boolean putValue(K key, V value, long expire) {
        if (!timeouts) {
            throw new IllegalStateException("If you want to use this method, please set `timeouts` = true");
        }

        return cache.put(key, value, System.currentTimeMillis() + expire);
    }

    public boolean addOrReplace(K key, V old, V value) {
        return cache.addOrReplace(key, old, value);
    }

    public boolean addOrReplace(K key, V old, V value, long expire) {
        return cache.addOrReplace(key, old, value, System.currentTimeMillis() + expire);
    }

    public boolean putIfAbsent(K key, V value) {
        return cache.putIfAbsent(key, value);
    }

    public boolean putIfAbsent(K key, V value, long expire) {
        return cache.putIfAbsent(key, value, System.currentTimeMillis() + expire);
    }

    /**
     * This is effectively a shortcut to add all entries in the given map {@code m}.
     *
     * @param m entries to be added
     */
    public void putAll(Map<? extends K, ? extends V> m) {
        cache.putAll(m);
    }

    @Override
    public void removeValue(K key) {
        remove(key);
    }

    @Override
    public void removeAll() {
        removeAll(getKeys());
    }

    /**
     * Remove a single entry for the given key.
     *
     * @param key key of the entry to be removed. Must not be {@code null}.
     * @return {@code true}, if the entry has been removed, {@code false} otherwise
     */
    public boolean remove(K key) {
        return cache.remove(key);
    }

    /**
     * This is effectively a shortcut to remove the entries for all keys given in the iterable {@code keys}.
     *
     * @param keys keys to be removed
     */
    public void removeAll(Iterable<K> keys) {
        cache.removeAll(keys);
    }

    /**
     * Removes all entries from the cache.
     */
    @Override
    public void cleanUp() {
        cache.clear();
    }

    /**
     * Get the value for a given key.
     *
     * @param key key of the entry to be retrieved. Must not be {@code null}.
     * @return either the non-{@code null} value or {@code null} if no entry for the requested key exists
     */
    @Override
    public V getValue(K key) {
        return cache.get(key);
    }

    /**
     * Get value and return default value if not exist
     *
     * @param key          key
     * @param defaultValue default value
     * @return V
     */
    @Override
    public V getValueOrDefault(K key, V defaultValue) {
        V value = getValue(key);
        if (null == value) {
            return defaultValue;
        } else {
            return value;
        }
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
        synchronized (this) {
            if (null == getValue(key)) {
                result = defaultValueSupplier.get();
                putValue(key, result);
            }
        }
        return result;
    }

    /**
     * Checks whether an entry for a given key exists.
     * Usually, this is more efficient than testing for {@code null} via {@link #getValue(Object)}.
     *
     * @param key key of the entry to be retrieved. Must not be {@code null}.
     * @return either {@code true} if an entry for the given key exists or {@code false} if no entry for the requested key exists
     */
    @Override
    public boolean containsKey(K key) {
        return cache.containsKey(key);
    }

    @Override
    public Set<K> getKeys() {
        Set<K> keys = new HashSet<>();
        for (CloseableIterator<K> it = keyIterator(); it.hasNext(); ) {
            keys.add(it.next());
        }
        return keys;
    }

    @Override
    public Collection<V> getValues() {
        Set<K> keys = getKeys();
        List<V> values = new ArrayList<>(keys.size());
        for (K key : keys) {
            values.add(getValue(key));
        }
        return values;
    }

    // cache loader support
    public Future<V> getWithLoaderAsync(K key) {
        if (null == cacheLoader) {
            throw new UnsupportedOperationException("Method `cacheLoader()` returns null, the cache does not support loading values!");
        }
        return cache.getWithLoaderAsync(key, cacheLoader);
    }

    public Future<V> getWithLoaderAsync(K key, long expire) {
        if (null == cacheLoader) {
            throw new UnsupportedOperationException("Method `cacheLoader()` returns null, the cache does not support loading values!");
        }
        return cache.getWithLoaderAsync(key, cacheLoader, System.currentTimeMillis() + expire);
    }

    public V getWithLoader(K key) throws InterruptedException, ExecutionException {
        if (null == cacheLoader) {
            throw new UnsupportedOperationException("Method `cacheLoader()` returns null, the cache does not support loading values!");
        }
        return cache.getWithLoader(key, cacheLoader);
    }

    public V getWithLoader(K key, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (null == cacheLoader) {
            throw new UnsupportedOperationException("Method `cacheLoader()` returns null, the cache does not support loading values!");
        }
        return cache.getWithLoader(key, cacheLoader, timeout, unit);
    }

    // iterators

    /**
     * Builds an iterator over the N most recently used keys returning deserialized objects.
     * You must call {@code close()} on the returned iterator.
     * <p>
     * Note: During a rehash, the implementation might return keys twice or not at all.
     * </p>
     *
     * @param n the N most recently used keys
     * @return closeable iterator over the keys
     */
    public CloseableIterator<K> hotKeyIterator(int n) {
        return cache.hotKeyIterator(n);
    }

    /**
     * Builds an iterator over all keys returning deserialized objects.
     * You must call {@code close()} on the returned iterator.
     * <p>
     * Note: During a rehash, the implementation might return keys twice or not at all.
     * </p>
     *
     * @return closeable iterator over the keys
     */
    public CloseableIterator<K> keyIterator() {
        return cache.keyIterator();
    }

    public void resetStatistics() {
        cache.resetStatistics();
    }

    @Override
    public long getSize() {
        return cache.size();
    }

    public int[] hashTableSizes() {
        return cache.hashTableSizes();
    }

    public long[] perSegmentSizes() {
        return cache.perSegmentSizes();
    }

    public EstimatedHistogram getBucketHistogram() {
        return cache.getBucketHistogram();
    }

    public int segments() {
        return cache.segments();
    }

    @Override
    public long getMaxSize() {
        return cache.capacity();
    }

    @Override
    public long memUsed() {
        return cache.memUsed();
    }

    public long freeCapacity() {
        return cache.freeCapacity();
    }

    public float loadFactor() {
        return cache.loadFactor();
    }

    public OHCacheStats stats() {
        return cache.stats();
    }

    /**
     * Modify the cache's capacity.
     * Lowering the capacity will not immediately remove any entry nor will it immediately free allocated (off heap) memory.
     * <p>
     * Future operations will even allocate in flight, temporary memory - i.e. setting capacity to 0 does not
     * disable the cache, it will continue to work but cannot add more data.
     * </p>
     *
     * @param capacity the new capacity
     */
    public void resetCapacity(long capacity) {
        cache.setCapacity(capacity);
    }

}
