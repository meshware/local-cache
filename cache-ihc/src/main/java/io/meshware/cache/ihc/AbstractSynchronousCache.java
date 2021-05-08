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
public abstract class AbstractSynchronousCache<K, V, X> extends AbstractLoadingCache<K, V> implements SynchronousCache<K, V, X> {

    /**
     * Get value with sync value
     *
     * @param key       key
     * @param syncValue sync value
     * @return V
     * @throws Exception exception
     */
    @Override
    public V getValue(K key, X syncValue) throws Exception {
        if (effectiveCheck(key, syncValue)) {
            return getValue(key);
        } else {
            synchronized (this) {
                if (!effectiveCheck(key, syncValue)) {
                    removeValue(key);
                    getSyncKeyLocalCache().putValue(key, syncValue);
                    if (log.isInfoEnabled()) {
                        log.info("[缓存同步]数据同步Key不一致，已更新！Cache={}, Key={}, SyncValue={}", getName(), key, syncValue);
                    }
                }
                return getValue(key);
            }
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
    @Synchronized
    public void putValue(K key, V value, X syncValue) {
        getSyncKeyLocalCache().putValue(key, syncValue);
        putValue(key, value);
    }
}
