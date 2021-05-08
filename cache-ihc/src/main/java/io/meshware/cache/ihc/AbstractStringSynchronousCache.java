package io.meshware.cache.ihc;

import io.meshware.cache.api.LocalCache;
import io.meshware.cache.ihc.impl.DefaultSyncKeyLocalCache;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

/**
 * Abstract Synchronous Cache (Cache key and synchronous keys are all String types)
 *
 * @author Zhiguo.Chen
 */
@Slf4j
@Data
@Accessors(chain = true)
public abstract class AbstractStringSynchronousCache<V> extends AbstractSynchronousCache<String, V, String> {

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
}
