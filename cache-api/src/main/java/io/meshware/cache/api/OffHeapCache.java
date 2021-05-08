package io.meshware.cache.api;

/**
 * OffHeapCache
 *
 * @author Zhiguo.Chen
 * @version 20210219
 */
public interface OffHeapCache<K, V> extends LocalCache<K, V> {

    boolean putValue(K key, V value, long expire);

    long memUsed();
}
