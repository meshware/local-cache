package io.meshware.cache.ihc;

import io.meshware.cache.api.SynchronousCache;
import lombok.Data;
import lombok.Synchronized;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

/**
 * Abstract Synchronous Cache
 *
 * @author Zhiguo.Chen
 */
@Slf4j
@Data
@Accessors(chain = true)
public abstract class AbstractSynchronousCache<K, V, X> extends AbstractLoadingCache<K, V> implements SynchronousCache<K, V, X> {

    /**
     * Get value with sync value
     *
     * @param key
     * @param syncValue
     * @return V
     */
    @Override
    public V getValue(K key, X syncValue) throws Exception {
        if (effectiveCheck(key, syncValue)) {
            return getValue(key);
        } else {
            synchronized (this) {
                if (!effectiveCheck(key, syncValue)) {
                    removeValue(key);
                    getSyncKeyLocalCache().putValue(key, syncValue);
                    if (log.isInfoEnabled()) {
                        log.info("[缓存同步]数据同步Key不一致，已更新！Cache={}, Key={}, SyncValue={}", getName(), key, syncValue);
                    }
                }
                return getValue(key);
            }
        }
    }

    /**
     * Put value with sync value
     *
     * @param key
     * @param value
     * @param syncValue
     */
    @Override
    @Synchronized
    public void putValue(K key, V value, X syncValue) {
        getSyncKeyLocalCache().putValue(key, syncValue);
        putValue(key, value);
    }
}
