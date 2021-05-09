package io.meshware.cache.sample.springboot.cache;

import com.github.benmanes.caffeine.cache.Cache;
import io.meshware.cache.ihc.AbstractCommonCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Description
 *
 * @author Zhiguo.Chen
 */
@Slf4j
@Component
public class CommonTestCache extends AbstractCommonCache<String, String> {

    /**
     * Set a name for the cache
     *
     * @return cache name
     */
    @Override
    public String getName() {
        return "commonTestCache";
    }

    /**
     * Init cache config
     */
    @Override
    public void initConfig() {
        this.setMaxSize(10);
        this.setExpireDurationAfterWrite(30);
        this.setExpireTimeUnit(TimeUnit.SECONDS);
    }

    /**
     * Init cache
     *
     * @param cache cache
     */
    @Override
    public void initCache(Cache<String, String> cache) {
        cache.put("aaa", "ooo");
    }

}
