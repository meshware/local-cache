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
package io.meshware.cache.redis.publisher;

import io.meshware.cache.api.manager.CacheMessagePublisher;
import io.meshware.cache.spring.event.CacheDiscardEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.CompletableFuture;

/**
 * Redis Cache Message Publisher
 *
 * @author Zhiguo.Chen
 */
@Slf4j
public class RedisMessagePublisher implements CacheMessagePublisher {

    private final StringRedisTemplate stringRedisTemplate;

    @Autowired
    public RedisMessagePublisher(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * Send discard cache message
     *
     * @param channelName channel name
     * @param cacheName   cache name
     * @param deleteKey   delete key
     * @return CompletableFuture
     */
    @Override
    public CompletableFuture<Void> sendDiscardCacheMessage(String channelName, String cacheName, String deleteKey) {
        CacheDiscardEntity cacheDiscard = new CacheDiscardEntity(cacheName, deleteKey);
        try {
            stringRedisTemplate.convertAndSend(channelName, cacheDiscard.toString());
            log.info("[Cache Discard]Send cache discard message success! channelName:{}, message:{}", channelName, cacheDiscard.toString());
        } catch (Exception e) {
            log.error("[Cache Discard]Failed to send cache discard message! channelName:{}, message:{}", channelName, cacheDiscard.toString(), e);
            CompletableFuture<Void> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
        return CompletableFuture.completedFuture(null);
    }
}
