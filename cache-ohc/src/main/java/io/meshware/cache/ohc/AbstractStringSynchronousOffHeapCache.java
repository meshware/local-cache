package io.meshware.cache.ohc;

import io.meshware.cache.api.LocalCache;
import io.meshware.cache.ihc.impl.DefaultSyncKeyLocalCache;
import io.meshware.cache.ohc.serializer.StringSerializer;
import org.caffinitas.ohc.CacheSerializer;

/**
 * AbstractStringSynchronousOffHeapCache
 *
 * @author Zhiguo.Chen
 * @version 20210430
 */
public abstract class AbstractStringSynchronousOffHeapCache<V> extends AbstractSynchronousOffHeapCache<String, V, String> {

    private LocalCache<String, String> defaultSyncKeyCache = new DefaultSyncKeyLocalCache();

    /**
     * Get Sync Key local cache storage
     *
     * @return
     */
    @Override
    public LocalCache<String, String> getSyncKeyLocalCache() {
        return defaultSyncKeyCache;
    }

    @Override
    public CacheSerializer<String> keySerializer() {
        return new StringSerializer();
    }
}
