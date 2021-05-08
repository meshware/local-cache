package io.meshware.cache.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * Synchronous Cache interface
 *
 * <p>
 * You can compare the synchronization key of the local cache and the incoming key(syncValue),
 * and then determine whether to reload the remote data to the local cache.
 * </p>
 *
 * @author Zhiguo.Chen
 */
public interface SynchronousCache<K, V, X> extends LocalCache<K, V> {

    Logger log = LoggerFactory.getLogger(SynchronousCache.class);

    /**
     * Get Sync Key local cache storage
     *
     * @return LocalCache
     */
    LocalCache<K, X> getSyncKeyLocalCache();

    /**
     * Check value effective or not
     *
     * @param valueKey
     * @param syncValue
     * @return boolean true(effective value)|false(invalid value)
     */
    default boolean effectiveCheck(K valueKey, X syncValue) {
        try {
            if (Objects.isNull(syncValue) || !StringUtils.hasText(syncValue.toString())) {
                return true;
            }
            if (!syncValue.equals(getSyncKeyLocalCache().getValue(valueKey))) {
                return false;
            }
        } catch (Exception e) {
            log.error("Check value refresh error, valueKey:{}, syncValue:{}", valueKey, syncValue, e);
        }
        return true;
    }

    /**
     * Get value with sync value
     *
     * @param key
     * @param syncValue
     * @return V
     */
    V getValue(K key, X syncValue) throws Exception;

    /**
     * Put value with sync value
     *
     * @param key
     * @param value
     * @param syncValue
     */
    void putValue(K key, V value, X syncValue);
}
