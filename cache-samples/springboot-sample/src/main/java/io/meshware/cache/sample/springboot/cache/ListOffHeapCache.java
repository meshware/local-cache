package io.meshware.cache.sample.springboot.cache;

import com.google.common.collect.Lists;
import io.meshware.cache.ohc.StringKeyOffHeapCache;
import io.meshware.cache.ohc.serializer.protostuff.ProtostuffObjectSerializer;
import io.meshware.cache.sample.springboot.entity.TestEntity;
import org.caffinitas.ohc.CacheSerializer;

import java.util.ArrayList;
import java.util.List;

/**
 * ObjectOffHeapCache
 *
 * @author Zhiguo.Chen
 * @version 20210219
 */
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
        this.timeouts = true;
        this.defaultTTLmillis = 300000;
        this.capacity = 67108864 * 4;
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
