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
     * @param channelName channel name
     * @param cacheName   cache name
     * @param deleteKey   delete key
     * @return CompletableFuture
     */
    CompletableFuture<Void> sendDiscardCacheMessage(String channelName, String cacheName, String deleteKey);
}
