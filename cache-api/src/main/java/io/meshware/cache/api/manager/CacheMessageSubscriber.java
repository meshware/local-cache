package io.meshware.cache.api.manager;

import java.util.concurrent.CompletableFuture;

/**
 * Cache Message Subscriber
 *
 * @author Zhiguo.Chen
 */
public interface CacheMessageSubscriber {

    /**
     * Add subscriber
     *
     * @param runnable thread
     * @return CompletableFuture
     */
    CompletableFuture<Void> addSubscriber(Runnable runnable);
}
