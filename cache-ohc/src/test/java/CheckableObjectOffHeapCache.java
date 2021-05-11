import io.meshware.cache.api.LocalCache;
import io.meshware.cache.ohc.AbstractStringSynchronousOffHeapCache;
import io.meshware.cache.ohc.serializer.ObjectSerializer;
import org.caffinitas.ohc.CacheSerializer;

/**
 * ObjectOffHeapCache
 *
 * @author Zhiguo.Chen
 * @version 20210310
 */
public class CheckableObjectOffHeapCache extends AbstractStringSynchronousOffHeapCache<TestEntity> {

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
        this.timeouts = true;
        this.defaultTTLmillis = 300;
        // this.capacity = 100000;
    }

    @Override
    public CacheSerializer<TestEntity> valueSerializer() {
        return new ObjectSerializer<>();
    }

    @Override
    public TestEntity loadData(String key) {
        return null;
    }

    /**
     * Get Sync Key local cache storage
     *
     * @return
     */
    @Override
    public LocalCache<String, String> getSyncValueLocalCache() {
        return null;
    }
}
