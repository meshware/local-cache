package io.meshware.cache.sample.springboot.cache;

import com.github.benmanes.caffeine.cache.Expiry;
import com.github.benmanes.caffeine.cache.LoadingCache;
import io.meshware.cache.api.LocalCache;
import io.meshware.cache.api.RedisCache;
import io.meshware.cache.ihc.AbstractStringSynchronousCache;
import io.meshware.cache.sample.springboot.entity.TestEntity;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * SynchronousObjectCache
 *
 * @author Zhiguo.Chen
 * @version 20210512
 */
@Slf4j
@Component
public class SynchronousObjectCache extends AbstractStringSynchronousCache<TestEntity> {

    AtomicLong atomicInteger = new AtomicLong(0);

    @Autowired
    private SyncPairLocalCache syncPairLocalCache;

    @Autowired
    private RedisCache redisCache;

    /**
     * Init cache config
     */
    @Override
    public void initConfig() {
        this.setExpireTimeUnit(TimeUnit.SECONDS);
        this.setExpireDurationAfterWrite(30);
        // this.setExpireDurationAfterAccess(30);
        this.expirySupplier = () -> new Expiry<String, TestEntity>() {
            @Override
            public long expireAfterCreate(@NonNull String s, @NonNull TestEntity testEntity, long l) {
                return 1000;
            }

            @Override
            public long expireAfterUpdate(@NonNull String s, @NonNull TestEntity testEntity, long l, @NonNegative long l1) {
                return 1000;
            }

            @Override
            public long expireAfterRead(@NonNull String s, @NonNull TestEntity testEntity, long l, @NonNegative long l1) {
                return 1000;
            }
        };
    }

    /**
     * Init cache
     *
     * @param cache cache
     */
    @Override
    public void initCache(LoadingCache<String, TestEntity> cache) {

    }

    /**
     * Get Value when Expired
     *
     * @param key key
     * @return V Not null
     * @throws Exception exception
     */
    @Override
    protected TestEntity getValueWhenExpired(String key) throws Exception {
        return getValueWhenRefresh(key, null);
    }

    /**
     * Get data when the refresh event occurs
     * <p>
     * By this method, valid data delay can be completed,
     * Note that {@code ExpireDurationAfterWrite} and {@code ExpireDurationAfterRead} cannot be set.
     * </p>
     *
     * @param key      Key
     * @param oldValue Old value
     * @return V Not null
     * @throws Exception exception
     */
    @Override
    protected TestEntity getValueWhenRefresh(String key, TestEntity oldValue) throws Exception {
        String aa = redisCache.get("aa");
        TestEntity entity = new TestEntity();
        entity.setName(key);
        entity.setCreateTime(new Date());
        entity.setId(atomicInteger.incrementAndGet());
        return entity;
    }

    /**
     * Get sync pair local cache storage
     *
     * @return localCache
     */
    @Override
    public LocalCache<String, String> getSyncPairLocalCache() {
        return syncPairLocalCache;
    }
}
