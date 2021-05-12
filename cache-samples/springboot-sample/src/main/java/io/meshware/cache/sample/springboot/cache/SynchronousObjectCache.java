package io.meshware.cache.sample.springboot.cache;

import com.github.benmanes.caffeine.cache.LoadingCache;
import io.meshware.cache.api.LocalCache;
import io.meshware.cache.ihc.AbstractStringSynchronousCache;
import io.meshware.cache.sample.springboot.entity.TestEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
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

    /**
     * Init cache config
     */
    @Override
    public void initConfig() {
        this.setExpireTimeUnit(TimeUnit.SECONDS);
        this.setExpireDurationAfterWrite(30);
        // this.setExpireDurationAfterAccess(30);
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
     * Get Value When Expired
     *
     * @param key key
     * @return V Not null
     * @throws Exception exception
     */
    @Override
    protected TestEntity getValueWhenExpired(String key) throws Exception {
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
