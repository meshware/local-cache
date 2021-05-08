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
     * @param runnable
     * @return
     */
    CompletableFuture<Void> addSubscriber(Runnable runnable);
}
