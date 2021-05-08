import io.meshware.cache.ohc.StringKeyOffHeapCache;
import io.meshware.cache.ohc.serializer.protostuff.ProtostuffObjectSerializer;
import org.caffinitas.ohc.CacheSerializer;

import java.util.Date;

/**
 * ObjectOffHeapCache
 *
 * @author Zhiguo.Chen
 * @version 20210219
 */
public class ObjectOffHeapCache extends StringKeyOffHeapCache<TestEntity> {

    /**
     * Set a name for the cache
     *
     * @return
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
        this.throwOOME = true;
        this.timeouts = true;
        this.defaultTTLmillis = 300;
        // this.capacity = 100000;
    }

    @Override
    public CacheSerializer<TestEntity> valueSerializer() {
        return new ProtostuffObjectSerializer(TestEntity.class);
        // return new KryoObjectSerializer<>(TestEntity.class);
    }

    @Override
    public TestEntity loadData(String key) {
        TestEntity testEntity = TestEntity.builder().name(key).age(100).id(1098L).createTime(new Date()).build();
        // put(key, testEntity);
        return testEntity;
    }
}
