import io.meshware.cache.ohc.StringKeyOffHeapCache;
import io.meshware.cache.ohc.serializer.protostuff.ProtostuffObjectSerializer;
import org.assertj.core.util.Lists;
import org.caffinitas.ohc.CacheSerializer;

import java.util.ArrayList;
import java.util.Date;
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
     * @return
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
        this.capacity = 67108864 * 4 / 1000;
    }

    @Override
    public CacheSerializer<List<TestEntity>> valueSerializer() {
         return new ProtostuffObjectSerializer(ArrayList.class);
        // return new KryoObjectSerializer(ArrayList.class);
//        return new KryoObjectSerializer_KryoPool(ArrayList.class);
    }

    @Override
    public List<TestEntity> loadData(String key) {
        TestEntity entity1 = TestEntity.builder().id(100000L).name("test1").createTime(new Date()).age(20).build();
        TestEntity entity2 = TestEntity.builder().id(100001L).name("test2").createTime(new Date()).age(21).build();
        TestEntity entity3 = TestEntity.builder().id(100002L).name("test3").createTime(new Date()).age(22).build();
        ArrayList<TestEntity> list = Lists.newArrayList(entity1, entity2, entity3);
        return list;
    }
}
