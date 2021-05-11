package io.meshware.cache.sample.springboot.cache;

import io.meshware.cache.ohc.StringKeyOffHeapCache;
import io.meshware.cache.ohc.serializer.protostuff.ProtostuffObjectSerializer;
import io.meshware.cache.sample.springboot.entity.TestEntity;
import lombok.extern.slf4j.Slf4j;
import org.caffinitas.ohc.CacheSerializer;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * ObjectOffHeapCache
 *
 * @author Zhiguo.Chen
 * @version 20210219
 */
@Slf4j
@Component
public class ObjectOffHeapCache extends StringKeyOffHeapCache<TestEntity> {

    /**
     * Set a name for the cache
     *
     * @return cache name
     */
    @Override
    public String getName() {
        return "objectOffHeapCache";
    }

    /**
     * Init cache config
     */
    @Override
    public void initConfig() {
        this.setThrowOOME(true);
        this.setTimeouts(true);
        this.setDefaultTTLmillis(300);
        // this.capacity = 100000;
    }

    @Override
    public CacheSerializer<TestEntity> valueSerializer() {
        return new ProtostuffObjectSerializer(TestEntity.class);
        // return new KryoObjectSerializer<>(TestEntity.class);
    }

    @Override
    public TestEntity loadData(String key) {
        TestEntity testEntity = new TestEntity();
        testEntity.setName(key);
        testEntity.setAge(100);
        testEntity.setId(1990L);
        testEntity.setCreateTime(new Date());
        // put(key, testEntity);
        return testEntity;
    }
}
