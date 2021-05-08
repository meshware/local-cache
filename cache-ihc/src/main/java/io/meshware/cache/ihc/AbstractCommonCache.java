package io.meshware.cache.ihc;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import io.meshware.cache.api.LocalCache;
import io.meshware.cache.api.event.CacheDiscardEntity;
import io.meshware.cache.api.event.CacheDiscardEvent;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.event.EventListener;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Abstract Common Cache
 *
 * @author Zhiguo.Chen
 */
@Slf4j
@Data
@Accessors(chain = true)
public abstract class AbstractCommonCache<K, V> implements LocalCache<K, V>, InitializingBean {

    /**
     * 缓存自动刷新周期
     */
    protected int refreshDuration = -1;

    /**
     * 缓存刷新周期时间格式
     */
    protected TimeUnit refreshTimeUnit = TimeUnit.MINUTES;

    /**
     * 缓存过期时间（创建后）
     */
    protected int expireDurationAfterWrite = -1;

    /**
     * 缓存过期时间（访问后）
     */
    protected int expireDurationAfterAccess = -1;

    /**
     * 缓存刷新周期时间格式
     */
    protected TimeUnit expireTimeUnit = TimeUnit.HOURS;

    /**
     * Cache max size
     */
    protected long maxSize = 10000;

    /**
     * Cache instance
     */
    private volatile Cache<K, V> cache = null;


    @Override
    public void afterPropertiesSet() throws Exception {
        initConfig();
        init();
    }

    /**
     * Init cache config
     */
    public abstract void initConfig();

    /**
     * Init cache instance
     */
    private synchronized void init() {
        Caffeine<K, V> cacheBuilder = Caffeine.newBuilder().maximumSize(maxSize).removalListener(
                (key, value, removalCause) -> whenRemove(key, value, removalCause));
        if (refreshDuration > 0) {
            cacheBuilder = cacheBuilder.refreshAfterWrite(refreshDuration, refreshTimeUnit);
        }
        if (expireDurationAfterWrite > 0) {
            cacheBuilder = cacheBuilder.expireAfterWrite(expireDurationAfterWrite, expireTimeUnit);
        }
        if (expireDurationAfterAccess > 0) {
            cacheBuilder = cacheBuilder.expireAfterAccess(expireDurationAfterAccess, expireTimeUnit);
        }
        cache = cacheBuilder.build();
        //Init cache
        initCache(cache);
    }

    /**
     * Init cache
     *
     * @param cache cache
     */
    public abstract void initCache(Cache<K, V> cache);

    @Override
    public V getValue(K key) {
        return getCache().getIfPresent(key);
    }

    @Override
    public void putValue(K key, V value) {
        getCache().put(key, value);
    }

    @Override
    public V getValueOrDefault(K key, V defaultValue) {
        try {
            V result = getValue(key);
            return result == null ? defaultValue : result;
        } catch (Exception e) {
            log.error("从内存缓存中获取内容时发生异常，key: " + key, e);
            return defaultValue;
        }
    }

    @Override
    public void removeValue(K key) {
        if (log.isInfoEnabled()) {
            log.info("The key[{}] of the current cache has been discarded! Cache class:{}", key, this.getClass().getSimpleName());
        }
        this.getCache().invalidate(key);
    }

    @Override
    public void removeAll() {
        if (log.isInfoEnabled()) {
            log.info("The cache will be discarded! Cache class:{}", this.getClass().getSimpleName());
        }
        this.getCache().invalidateAll();
    }

    @Override
    public void cleanUp() {
        this.getCache().cleanUp();
    }

    @Override
    public Set<K> getKeys() {
        return this.getCache().asMap().keySet();
    }

    @Override
    public Collection<V> getValues() {
        return this.getCache().asMap().values();
    }

    @Override
    public long getSize() {
        return this.getCache().estimatedSize();
    }

    @Override
    public boolean containsKey(K key) {
        return this.getCache().asMap().containsKey(key);
    }

    /**
     * Create cache instance
     *
     * @return Cache
     */
    private Cache<K, V> getCache() {
        if (cache == null) {
            synchronized (this) {
                if (cache == null) {
                    init();
                }
            }
        }
        return cache;
    }

    /**
     * 缓存移除监听器
     *
     * @param key          key
     * @param value        value
     * @param removalCause remove cause
     */
    public void whenRemove(@Nullable K key, @Nullable V value, @NonNull RemovalCause removalCause) {
        if (log.isInfoEnabled()) {
            log.info("[RemoveCallback]Remove cache key:{}, value:{}, cause:{}, cacheName={}", key, value, removalCause, getName());
        }
    }

    @EventListener
    public void discardCacheByKey(CacheDiscardEvent cacheDiscardEvent) {
        CacheDiscardEntity cacheDiscard = (CacheDiscardEntity) cacheDiscardEvent.getSource();
        if (log.isInfoEnabled()) {
            log.info("Receive event:{}, deleteKey:{}, current cache:{}", cacheDiscard.getClass(), cacheDiscard.getDeleteKey(), this.getClass().getSimpleName());
        }
        if (this.getName().equals(cacheDiscard.getCacheName())) {
            removeValue((K) cacheDiscard.getDeleteKey());
        }
    }
}
