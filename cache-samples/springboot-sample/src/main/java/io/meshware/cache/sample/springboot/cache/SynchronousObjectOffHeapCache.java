package io.meshware.cache.sample.springboot.cache;

import io.meshware.cache.api.LocalCache;
import io.meshware.cache.ohc.AbstractStringSynchronousOffHeapCache;
import io.meshware.cache.ohc.serializer.ObjectSerializer;
import io.meshware.cache.sample.springboot.entity.TestEntity;
import lombok.extern.slf4j.Slf4j;
import org.caffinitas.ohc.CacheSerializer;
import org.springframework.stereotype.Component;

/**
 * ObjectOffHeapCache
 *
 * @author Zhiguo.Chen
 * @version 20210310
 */
@Slf4j
@Component
public class SynchronousObjectOffHeapCache extends AbstractStringSynchronousOffHeapCache<TestEntity> {

    /**
     * Set a name for the cache
     *
     * @return cache name
     */
    @Override
    public String getName() {
        return "synchronousObjectOffHeapCache";
    }

    /**
     * Init cache config
     */
    @Override
    public void initConfig() {
        this.setTimeouts(true);
        this.setDefaultTTLmillis(300);
    }

    @Override
    public CacheSerializer<TestEntity> valueSerializer() {
        return new ObjectSerializer<>();
    }

    @Override
    public TestEntity loadData(String key) {
        return null;
    }

}
