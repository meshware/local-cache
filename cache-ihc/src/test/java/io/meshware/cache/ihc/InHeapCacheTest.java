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

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.junit.Assert;
import org.junit.Test;

public class InHeapCacheTest {

    @Test
    public void testCommonCache() throws Exception {
        AbstractCommonCache<String, String> cache = new AbstractCommonCache<String, String>() {
            @Override
            public void initCache(Cache<String, String> cache) {
            }

            @Override
            public void initConfig() {
                setMaxSize(100);
                setName("testCommonCache");
            }
        };

        cache.buildCache();

        cache.putValue("key1", "value1");
        Assert.assertEquals("value1", cache.getValue("key1"));
        
        Assert.assertEquals("default", cache.getValueOrDefault("key2", "default"));
        
        Assert.assertEquals("supplier", cache.getValueOrSupplier("key3", () -> "supplier"));
        Assert.assertEquals("supplier", cache.getValue("key3")); // Ensure it was put in the cache
        
        cache.removeValue("key1");
        Assert.assertNull(cache.getValue("key1"));
    }

    @Test
    public void testLoadingCache() throws Exception {
        AbstractLoadingCache<String, String> cache = new AbstractLoadingCache<String, String>() {
            @Override
            public void initCache(LoadingCache<String, String> cache) {
            }

            @Override
            public void initConfig() {
                setMaxSize(100);
                setName("testLoadingCache");
            }

            @Override
            protected String getValueWhenExpired(String key) throws Exception {
                return "loaded_" + key;
            }
        };

        cache.buildCache();

        // Should automatically load via getValueWhenExpired
        Assert.assertEquals("loaded_key1", cache.getValue("key1"));
        
        // Manual put overrides loading
        cache.putValue("key2", "manual_value");
        Assert.assertEquals("manual_value", cache.getValue("key2"));
    }
}
