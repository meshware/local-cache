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

import io.meshware.cache.api.SynchronousCache;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.concurrent.locks.StampedLock;

/**
 * AbstractSynchronousOffHeapCache
 *
 * @author Zhiguo.Chen
 * @version 20210310
 */
public abstract class AbstractSynchronousOffHeapCache<K, V, X, Y> extends AbstractOffHeapCache<K, V> implements SynchronousCache<K, V, X, Y> {

    private final StampedLock stampedLock = new StampedLock();

    /**
     * Get value with sync value
     *
     * @param key       key
     * @param syncValue sync value
     * @return V
     * @throws Exception exception
     */
    @Override
    public V getValueWithSyncValue(K key, Y syncValue) throws Exception {
        if (effectiveCheck(key, syncValue)) {
            V v = getValue(key);
            if (Objects.nonNull(v)) {
                return v;
            }
        }
        // boolean wasFirst = lock();
        long stamp = stampedLock.writeLock();
        try {
            if (!effectiveCheck(key, syncValue)) {
                removeValue(key);
                getSyncValueLocalCache().putValue(key, syncValue);
                if (log.isInfoEnabled()) {
                    log.info("[OHC同步]数据同步Key不一致，已更新！Cache={}, Key={}, SyncValue={}", getName(), key, syncValue);
                }
            }
            return getWithLoader(key);
        } finally {
            // unlock(wasFirst);
            stampedLock.unlockWrite(stamp);
        }
    }

    /**
     * Get value with sync key
     *
     * @param key     key
     * @param syncKey syncKey
     * @return V
     * @throws Exception e
     */
    @Override
    public V getValueWithSyncKey(K key, X syncKey) throws Exception {
        if (null != getSyncPairLocalCache()) {
            Y syncValue = getSyncPairLocalCache().getValueOrDefault(syncKey, null);
            return getValueWithSyncValue(key, syncValue);
        } else {
            if (log.isWarnEnabled()) {
                log.warn("该同步型缓存未提供'SyncPairLocalCache'具体实现，无法提供自动同步功能！cacheName={}", getName());
            }
            return getValue(key);
        }
    }

    /**
     * Put value with sync value
     *
     * @param key       key
     * @param value     value
     * @param syncValue sync value
     */
    @Override
    public void putValue(K key, V value, Y syncValue) {
        long stamp = stampedLock.writeLock();
        try {
            if (null != getSyncValueLocalCache()) {
                getSyncValueLocalCache().putValue(key, syncValue);
            } else {
                if (log.isWarnEnabled()) {
                    log.warn("该同步型缓存未提供'SyncValueLocalCache'具体实现，无法提供自动同步功能！cacheName={}", getName());
                }
            }
            putValue(key, value);
        } finally {
            stampedLock.unlockWrite(stamp);
        }
    }

    private final boolean unlocked = false;
    private volatile long lock;
    private static final AtomicLongFieldUpdater<AbstractSynchronousOffHeapCache> lockFieldUpdater =
            AtomicLongFieldUpdater.newUpdater(AbstractSynchronousOffHeapCache.class, "lock");

    private boolean lock() {
        if (unlocked) {
            return false;
        }

        long t = Thread.currentThread().getId();

        if (t == lockFieldUpdater.get(this)) {
            return false;
        }
        while (true) {
            if (lockFieldUpdater.compareAndSet(this, 0L, t)) {
                return true;
            }

            while (lockFieldUpdater.get(this) != 0L) {
                Thread.yield();
            }
        }
    }

    private void unlock(boolean wasFirst) {
        if (unlocked || !wasFirst) {
            return;
        }
        long t = Thread.currentThread().getId();
        boolean r = lockFieldUpdater.compareAndSet(this, t, 0L);
        assert r;
    }

}
