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
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.locks.StampedLock;

/**
 * AbstractSynchronousOffHeapCache
 *
 * @author Zhiguo.Chen
 * @version 20210310
 */
@Slf4j
@Data
@Accessors(chain = true)
public abstract class AbstractSynchronousOffHeapCache<K, V, X, Y> extends AbstractOffHeapCache<K, V>
        implements SynchronousCache<K, V, X, Y> {

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
        long readLockStamp = stampedLock.readLock();
        try {
            if (effectiveCheck(key, syncValue)) {
                V v = getValue(key);
                if (Objects.nonNull(v)) {
                    return v;
                }
            }
        } finally {
            stampedLock.unlock(readLockStamp);
        }
        long writeLockStamp = stampedLock.writeLock();
        try {
            if (!effectiveCheck(key, syncValue)) {
                removeValue(key);
                getSyncValueLocalCache().putValue(key, syncValue);
                if (log.isInfoEnabled()) {
                    log.info("Cache synced due to inconsistent sync value. Cache={}, Key={}, SyncValue={}", getName(), key, syncValue);
                }
            }
            return getWithLoader(key);
        } finally {
            stampedLock.unlockWrite(writeLockStamp);
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
            log.warn("SyncPairLocalCache not provided, automatic sync unavailable. Cache={}", getName());
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
                log.warn("SyncValueLocalCache not provided, automatic sync unavailable. Cache={}", getName());
            }
            putValue(key, value);
        } finally {
            stampedLock.unlockWrite(stamp);
        }
    }

}
