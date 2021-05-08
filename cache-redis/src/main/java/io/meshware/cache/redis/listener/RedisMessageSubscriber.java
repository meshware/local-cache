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
package io.meshware.cache.redis.listener;

import com.alibaba.fastjson.JSON;
import io.meshware.cache.api.event.CacheDiscardEntity;
import io.meshware.cache.api.manager.AbstractCacheSyncManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

/**
 * RedisMessageSubscriber
 *
 * @author Zhiguo.Chen
 */
@Slf4j
public class RedisMessageSubscriber extends AbstractCacheSyncManager implements MessageListener {

    public RedisMessageSubscriber(ApplicationContext applicationContext) {
        super(applicationContext);
    }

    @Override
    public void onMessage(final Message message, final byte[] pattern) {
        log.info("接收到channel:{}, message:{}", new String(message.getChannel(), StandardCharsets.UTF_8), message.toString());
        CacheDiscardEntity cacheDiscard = JSON.parseObject(message.toString(), CacheDiscardEntity.class);
        doCacheDiscard(cacheDiscard);
    }

    /**
     * Init Manager
     *
     * @return CompletableFuture
     */
    @Override
    public CompletableFuture<Void> initManager() {
        return CompletableFuture.completedFuture(null);
    }
}
