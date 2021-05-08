package io.meshware.cache.redis.publisher;

import io.meshware.cache.api.event.CacheDiscardEntity;
import io.meshware.cache.api.manager.CacheMessagePublisher;
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
//@Component
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
        CacheDiscardEntity cacheDiscard = new CacheDiscardEntity(cacheName, deleteKey); //.setDeleteKey("AAA").setCacheClass(TestCache.class);
        stringRedisTemplate.convertAndSend(channelName, cacheDiscard.toString());
        log.info("[Cache Discard]Send cache discard message success! channelName:{}, message:{}", channelName, cacheDiscard.toString());
        return CompletableFuture.completedFuture(null);
    }
}
