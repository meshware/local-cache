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

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache manager
 *
 * @author Zhiguo.Chen
 */
@Slf4j
public class CacheManager {

    private static ApplicationContext applicationContext;

    private static Map<String, Cache> cacheMap = new ConcurrentHashMap<>();

    @Autowired
    public CacheManager(ApplicationContext applicationContext) {
        CacheManager.applicationContext = applicationContext;
        init();
    }

    public static void init() {
        if (applicationContext != null) {
            Map<String, Cache> beans = applicationContext.getBeansOfType(Cache.class);
            beans.forEach((beanName, cacheBean) -> addHandler(cacheBean, beanName));
            log.info("Init local cache finished, local cache count:{}", cacheMap.size());
        } else {
            log.error("ApplicationContext is null, please value ascribed first!");
        }
    }

    public static void addHandler(Cache cache, String defaultName) {
        String cacheName = StringUtils.hasText(cache.getName()) ? defaultName : cache.getName();
        if (!cacheMap.containsKey(cacheName)) {
            cacheMap.putIfAbsent(cacheName, cache);
        } else {
            log.error("[######]CacheName[{}] has duplicate cache bean. This cache will be ignored (invalid)! handler class={}",
                    cacheName, cache.getClass().getCanonicalName());
        }
    }

    public static Cache getCache(String cacheName) {
        if (cacheMap.size() == 0) {
            init();
        }
        return cacheMap.get(cacheName);
    }
}
