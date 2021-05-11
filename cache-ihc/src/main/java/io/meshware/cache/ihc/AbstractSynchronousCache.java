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

import io.meshware.cache.api.SynchronousCache;
import lombok.Data;
import lombok.Synchronized;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

/**
 * Abstract Synchronous Cache
 *
 * @author Zhiguo.Chen
 */
@Slf4j
@Data
@Accessors(chain = true)
public abstract class AbstractSynchronousCache<K, V, X, Y> extends AbstractLoadingCache<K, V> implements SynchronousCache<K, V, X, Y> {

    /**
     * Get value with sync value
     *
     * @param key
     * @param syncValue
     * @return
     * @throws Exception
     */
    @Override
    public V getValueWithSyncValue(K key, Y syncValue) throws Exception {
        if (effectiveCheck(key, syncValue)) {
            return getValue(key);
        } else {
            if (null == getSyncValueLocalCache()) {
                if (log.isWarnEnabled()) {
                    log.warn("该同步型缓存未提供'SyncValueLocalCache'具体实现，无法提供自动同步功能！cacheName={}", getName());
                }
                return getValue(key);
            }
            synchronized (this) {
                if (!effectiveCheck(key, syncValue)) {
                    removeValue(key);
                    getSyncValueLocalCache().putValue(key, syncValue);
                    if (log.isInfoEnabled()) {
                        log.info("[缓存同步]数据同步Key不一致，已更新！Cache={}, Key={}, SyncValue={}", getName(), key, syncValue);
                    }
                }
                return getValue(key);
            }
        }
    }

    /**
     * Get value with sync value
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
     * @param key
     * @param value
     * @param syncValue
     */
    @Override
    @Synchronized
    public void putValue(K key, V value, Y syncValue) {
        if (null != getSyncValueLocalCache()) {
            getSyncValueLocalCache().putValue(key, syncValue);
        } else {
            if (log.isWarnEnabled()) {
                log.warn("该同步型缓存未提供'SyncValueLocalCache'具体实现，无法提供自动同步功能！cacheName={}", getName());
            }
        }
        putValue(key, value);
    }
}
