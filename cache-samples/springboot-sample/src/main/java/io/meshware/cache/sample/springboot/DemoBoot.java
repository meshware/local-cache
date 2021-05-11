package io.meshware.cache.sample.springboot;

import io.meshware.cache.sample.springboot.cache.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@SpringBootApplication(scanBasePackages = {"io.meshware.cache.sample"})
public class DemoBoot {

    public static void main(String[] args) throws Exception {
        System.setProperty("spring.profiles.active", "demo");
        ConfigurableApplicationContext run = SpringApplication.run(DemoBoot.class, args);
//        RedisCache redisCache = run.getBean(RedisCache.class);
//        redisCache.setex("aaa", 10000, "123");
//        log.info("Get value from redis by key={}, value={}", "aaa", redisCache.get("aaa"));

        // Ordinary local cache, data can be eliminated automatically according to the strategy. such as: ttl, lru...
        CommonTestCache commonTestCache = run.getBean(CommonTestCache.class);
        // On the basis of the ordinary cache function, data that is not in the local cache can be dynamically loaded according to the logic.
        SyncPairLocalCache syncPairLocalCache = run.getBean(SyncPairLocalCache.class);
        // In ordinary off-heap cache, both key and value are strings. Support data elimination strategy.
        StringOffHeapCache stringOffHeapCache = run.getBean(StringOffHeapCache.class);
        // In ordinary off-heap cache, key is string, value are object.
        ObjectOffHeapCache objectOffHeapCache = run.getBean(ObjectOffHeapCache.class);
        // In ordinary off-heap cache, key is string, value are list(array).
        ListOffHeapCache listOffHeapCache = run.getBean(ListOffHeapCache.class);

        AtomicLong counter = new AtomicLong(0);
        while (true) {
            commonTestCache.putValue(counter.toString(), counter.toString());
            log.info("向普通本地缓存中添加数据，第{}次，当前缓存数据量：{}", counter, commonTestCache.getSize());
            Thread.sleep(1000);
            counter.incrementAndGet();
        }
    }
}