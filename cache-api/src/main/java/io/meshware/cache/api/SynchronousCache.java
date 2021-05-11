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
package io.meshware.cache.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * Synchronous Cache interface
 *
 * <p>
 * You can compare the synchronization key of the local cache and the incoming key(syncValue),
 * and then determine whether to reload the remote data to the local cache.
 * </p>
 *
 * @author Zhiguo.Chen
 */
public interface SynchronousCache<K, V, X, Y> extends LocalCache<K, V> {

    Logger log = LoggerFactory.getLogger(SynchronousCache.class);

    /**
     * Get sync pair local cache storage
     *
     * @return local cache
     */
    LocalCache<X, Y> getSyncPairLocalCache();

    /**
     * Get sync value local cache storage
     *
     * @return local cache
     */
    LocalCache<K, Y> getSyncValueLocalCache();

    /**
     * Check value effective or not
     *
     * @param valueKey  value Key
     * @param syncValue sync value
     * @return boolean true(effective value)|false(invalid value)
     */
    default boolean effectiveCheck(K valueKey, Y syncValue) {
        try {
            if (Objects.isNull(syncValue) || !StringUtils.hasText(syncValue.toString())) {
                return true;
            }
            if (!syncValue.equals(getSyncValueLocalCache().getValue(valueKey))) {
                return false;
            }
        } catch (Exception e) {
            log.error("Check value refresh error, valueKey:{}, syncValue:{}", valueKey, syncValue, e);
        }
        return true;
    }

    /**
     * Get value with sync key
     *
     * @param key     key
     * @param syncKey syncKey
     * @return V
     * @throws Exception e
     */
    V getValueWithSyncKey(K key, X syncKey) throws Exception;

    /**
     * Get value with sync value
     *
     * @param key       key
     * @param syncValue syncValue
     * @return V
     * @throws Exception e
     */
    V getValueWithSyncValue(K key, Y syncValue) throws Exception;

    /**
     * Put value with sync value
     *
     * @param key       key
     * @param value     value
     * @param syncValue syncValue
     */
    void putValue(K key, V value, Y syncValue);
}
