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
package io.meshware.cache.api.manager;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.meshware.cache.api.event.CacheDiscardEntity;
import io.meshware.cache.api.event.CacheDiscardEvent;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.*;

/**
 * Cache Sync Manager
 *
 * @author Zhiguo.Chen
 */
@Slf4j
@Component
public abstract class AbstractCacheSyncManager implements CacheMessageSubscriber {

    protected static final ExecutorService pool;

    static {
        ThreadFactory nameThreadFactory = new ThreadFactoryBuilder().setNameFormat("cache-sync-pool-%d").build();
        pool = new ThreadPoolExecutor(5, 200, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1024), nameThreadFactory, new ThreadPoolExecutor.AbortPolicy());
    }

    private ApplicationContext applicationContext;

    private AbstractCacheSyncManager() {
    }

    /**
     * Init Manager
     *
     * @return CompletableFuture
     */
    @PostConstruct
    public abstract CompletableFuture<Void> initManager();

    @Autowired
    public AbstractCacheSyncManager(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public CompletableFuture<Void> addSubscriber(Runnable runnable) {
        pool.execute(runnable);
        return CompletableFuture.completedFuture(null);
    }

    public CompletableFuture<Void> doCacheDiscard(@NonNull CacheDiscardEntity cacheDiscard) {
        applicationContext.publishEvent(new CacheDiscardEvent(cacheDiscard));
        return CompletableFuture.completedFuture(null);
    }
}
