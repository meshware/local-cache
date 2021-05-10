package io.meshware.cache.sample.springboot.cache;

import io.meshware.cache.ohc.StringKeyOffHeapCache;
import io.meshware.cache.ohc.serializer.StringSerializer;
import org.caffinitas.ohc.CacheSerializer;

/**
 * StringOffHeapCache
 *
 * @author Zhiguo.Chen
 * @version 20210219
 */
public class StringOffHeapCache extends StringKeyOffHeapCache<String> {

    /**
     * Set a name for the cache
     *
     * @return cache name
     */
    @Override
    public String getName() {
        return "stringOffHeapCache";
    }

    /**
     * Init cache config
     */
    @Override
    public void initConfig() {
        this.setTimeouts(true);
        this.setDefaultTTLmillis(200);
    }

    @Override
    public CacheSerializer<String> valueSerializer() {
        return new StringSerializer();
    }

    @Override
    public String loadData(String key) {
        return null;
    }
}
