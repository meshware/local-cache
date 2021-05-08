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
public interface SynchronousCache<K, V, X> extends LocalCache<K, V> {

    Logger log = LoggerFactory.getLogger(SynchronousCache.class);

    /**
     * Get Sync Key local cache storage
     *
     * @return LocalCache
     */
    LocalCache<K, X> getSyncKeyLocalCache();

    /**
     * Check value effective or not
     *
     * @param valueKey  valueKey
     * @param syncValue sync value
     * @return boolean true(effective value)|false(invalid value)
     */
    default boolean effectiveCheck(K valueKey, X syncValue) {
        try {
            if (Objects.isNull(syncValue) || !StringUtils.hasText(syncValue.toString())) {
                return true;
            }
            if (!syncValue.equals(getSyncKeyLocalCache().getValue(valueKey))) {
                return false;
            }
        } catch (Exception e) {
            log.error("Check value refresh error, valueKey:{}, syncValue:{}", valueKey, syncValue, e);
        }
        return true;
    }

    /**
     * Get value with sync value
     *
     * @param key       key
     * @param syncValue sync value
     * @return V
     * @throws Exception exception
     */
    V getValue(K key, X syncValue) throws Exception;

    /**
     * Put value with sync value
     *
     * @param key       key
     * @param value     value
     * @param syncValue sync value
     */
    void putValue(K key, V value, X syncValue);
}
