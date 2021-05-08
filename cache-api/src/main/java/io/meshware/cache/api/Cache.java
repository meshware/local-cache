package io.meshware.cache.api;

/**
 * Cache
 *
 * @author Zhiguo.Chen
 */
public interface Cache {

    /**
     * Set a name for the cache
     *
     * @return
     */
    default String getName() {
        return this.getClass().getCanonicalName();
    }
}
