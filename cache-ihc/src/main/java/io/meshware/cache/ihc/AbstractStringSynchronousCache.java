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

import io.meshware.cache.api.LocalCache;
import io.meshware.cache.ihc.impl.DefaultSyncKeyLocalCache;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

/**
 * Abstract Synchronous Cache (Cache key and synchronous keys are all String types)
 *
 * @author Zhiguo.Chen
 */
@Slf4j
@Data
@Accessors(chain = true)
public abstract class AbstractStringSynchronousCache<V> extends AbstractSynchronousCache<String, V, String> {

    private LocalCache<String, String> defaultSyncKeyCache = new DefaultSyncKeyLocalCache();

    /**
     * Get Sync Key local cache storage
     *
     * @return LocalCache
     */
    @Override
    public LocalCache<String, String> getSyncKeyLocalCache() {
        return defaultSyncKeyCache;
    }
}
