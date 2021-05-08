package io.meshware.cache.api;

import java.util.Collection;
import java.util.Set;

/**
 * Local cache interface
 *
 * @author Zhiguo.Chen
 */
public interface LocalCache<K, V> extends Cache {

    V getValue(K key) throws Exception;

    V getValueOrDefault(K key, V defaultValue);

    void putValue(K key, V value);

    void removeValue(K key);

    void removeAll();

    void cleanUp();

    Set<K> getKeys();

    Collection<V> getValues();

    long getMaxSize();

    long getSize();

    boolean containsKey(K key);
}
