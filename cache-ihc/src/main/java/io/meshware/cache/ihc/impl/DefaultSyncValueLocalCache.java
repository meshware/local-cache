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
package io.meshware.cache.ihc.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.RemovalCause;
import io.meshware.cache.ihc.AbstractCommonCache;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * Default sync key local cache implementation
 *
 * @author Zhiguo.Chen
 */
@Slf4j
public class DefaultSyncValueLocalCache extends AbstractCommonCache<String, String> {

    @Override
    public String getName() {
        return "defaultSyncValueLocalCache";
    }

    @Override
    public void initConfig() {
        this.setMaxSize(10000);
        this.setExpireTimeUnit(TimeUnit.HOURS);
        this.setExpireDurationAfterWrite(8);
    }

    @Override
    public void initCache(Cache<String, String> cache) {

    }

    @Override
    public void whenRemove(String key, String value, RemovalCause removalCause) {
        if (removalCause == RemovalCause.SIZE || removalCause == RemovalCause.COLLECTED) {
            log.error("缓存失效！失效原因：{}, 缓存：{}", removalCause, getName());
        }
        super.whenRemove(key, value, removalCause);
    }
}