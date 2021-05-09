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
     * @return
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
        this.timeouts = true;
        this.defaultTTLmillis = 200;
        // this.chunkSize = 1000;
        // this.capacity = 671088640;
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
