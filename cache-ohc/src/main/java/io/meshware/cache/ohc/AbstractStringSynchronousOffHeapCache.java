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

import io.meshware.cache.api.LocalCache;
import io.meshware.cache.ihc.impl.DefaultSyncValueLocalCache;
import io.meshware.cache.ohc.serializer.StringSerializer;
import org.caffinitas.ohc.CacheSerializer;

/**
 * AbstractStringSynchronousOffHeapCache
 *
 * @author Zhiguo.Chen
 * @version 20210430
 */
public abstract class AbstractStringSynchronousOffHeapCache<V> extends AbstractSynchronousOffHeapCache<String, V, String, String> {

    /**
     * default sync value cache
     */
    private LocalCache<String, String> defaultSyncValueCache = new DefaultSyncValueLocalCache();

    /**
     * Get Sync Key local cache storage
     *
     * @return localCache
     */
    @Override
    public LocalCache<String, String> getSyncValueLocalCache() {
        return defaultSyncValueCache;
    }

    /**
     * Get sync pair local cache storage
     *
     * @return localCache
     */
    @Override
    public LocalCache<String, String> getSyncPairLocalCache() {
        return null;
    }

    /**
     * Key serializer
     *
     * @return CacheSerializer
     */
    @Override
    public CacheSerializer<String> keySerializer() {
        return new StringSerializer();
    }
}
