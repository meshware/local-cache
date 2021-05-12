package io.meshware.cache.sample.springboot.cache;

import io.meshware.cache.api.LocalCache;
import io.meshware.cache.ohc.AbstractStringSynchronousOffHeapCache;
import io.meshware.cache.ohc.serializer.ObjectSerializer;
import io.meshware.cache.sample.springboot.entity.TestEntity;
import lombok.extern.slf4j.Slf4j;
import org.caffinitas.ohc.CacheSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

/**
 * ObjectOffHeapCache
 *
 * @author Zhiguo.Chen
 * @version 20210310
 */
@Slf4j
@Component
public class SynchronousObjectOffHeapCache extends AbstractStringSynchronousOffHeapCache<TestEntity> {

    AtomicLong atomicInteger = new AtomicLong(0);

    @Autowired
    private SyncPairLocalCache syncPairLocalCache;

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
        this.setDefaultTTLmillis(30000);
    }

    @Override
    public CacheSerializer<TestEntity> valueSerializer() {
        return new ObjectSerializer<>();
    }

    @Override
    public TestEntity loadData(String key) {
        TestEntity entity = new TestEntity();
        entity.setName(key);
        entity.setCreateTime(new Date());
        entity.setId(atomicInteger.incrementAndGet());
        return entity;
    }

    @Override
    public LocalCache<String, String> getSyncPairLocalCache() {
        return syncPairLocalCache;
    }
}
