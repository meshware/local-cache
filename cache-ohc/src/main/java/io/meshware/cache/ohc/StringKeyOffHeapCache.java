package io.meshware.cache.ohc;

import io.meshware.cache.ohc.serializer.StringSerializer;
import org.caffinitas.ohc.CacheSerializer;

/**
 * String Key OffHeap Cache
 *
 * @author Zhiguo.Chen
 * @version 20210210
 */
public abstract class StringKeyOffHeapCache<V> extends AbstractOffHeapCache<String, V> {

    @Override
    public CacheSerializer<String> keySerializer() {
        return new StringSerializer();
    }
}
