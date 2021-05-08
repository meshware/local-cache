package io.meshware.cache.api.manager;

import java.util.concurrent.CompletableFuture;

/**
 * Cache Message Publisher
 *
 * @author Zhiguo.Chen
 */
public interface CacheMessagePublisher {

    /**
     * Send discard cache message
     *
     * @param channelName
     * @param cacheName
     * @param deleteKey
     * @return
     */
    CompletableFuture<Void> sendDiscardCacheMessage(String channelName, String cacheName, String deleteKey);
}
