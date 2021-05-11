package io.meshware.cache.sample.springboot.cache;

import com.google.common.collect.Lists;
import io.meshware.cache.ohc.StringKeyOffHeapCache;
import io.meshware.cache.ohc.serializer.protostuff.ProtostuffObjectSerializer;
import io.meshware.cache.sample.springboot.entity.TestEntity;
import lombok.extern.slf4j.Slf4j;
import org.caffinitas.ohc.CacheSerializer;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * ObjectOffHeapCache
 *
 * @author Zhiguo.Chen
 * @version 20210219
 */
@Slf4j
@Component
public class ListOffHeapCache extends StringKeyOffHeapCache<List<TestEntity>> {

    /**
     * Set a name for the cache
     *
     * @return cache name
     */
    @Override
    public String getName() {
        return "listOffHeapCache";
    }

    /**
     * Init cache config
     */
    @Override
    public void initConfig() {
        this.setTimeouts(true);
        this.setDefaultTTLmillis(300000);
        this.setCapacity(67108864 * 4);
    }

    @Override
    public CacheSerializer<List<TestEntity>> valueSerializer() {
         return new ProtostuffObjectSerializer(ArrayList.class);
        // return new KryoObjectSerializer(ArrayList.class);
//        return new KryoObjectSerializer_KryoPool(ArrayList.class);
    }

    @Override
    public List<TestEntity> loadData(String key) {
        return Lists.newCopyOnWriteArrayList();
    }
}
