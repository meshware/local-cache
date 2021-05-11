package io.meshware.cache.sample.springboot.cache;

import com.github.benmanes.caffeine.cache.LoadingCache;
import io.meshware.cache.ihc.AbstractLoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Support other local cache whether to update data auxiliary cache implementation.
 */
@Slf4j
@Component
public class SyncPairLocalCache extends AbstractLoadingCache<String, String> {

    @Override
    public void initConfig() {
        // The cached data will be deleted 5 seconds after being written.
        this.setExpireTimeUnit(TimeUnit.SECONDS);
        this.setExpireDurationAfterWrite(5);
    }

    @Override
    public void initCache(LoadingCache<String, String> cache) {

    }

    @Override
    protected String getValueWhenExpired(String key) throws Exception {
        // You can get the synchronization value through the synchronization key, for example, read it from remote redis.
        return key + "value";
    }

}
