package io.meshware.cache.sample.springboot;

import com.alibaba.fastjson.JSON;
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
        // In ordinary off-heap cache, key is string, value is object.
        ObjectOffHeapCache objectOffHeapCache = run.getBean(ObjectOffHeapCache.class);
        // In ordinary off-heap cache, key is string, value is list(array).
        ListOffHeapCache listOffHeapCache = run.getBean(ListOffHeapCache.class);

        SynchronousObjectOffHeapCache synchronousObjectOffHeapCache = run.getBean(SynchronousObjectOffHeapCache.class);
        SynchronousObjectCache synchronousObjectCache = run.getBean(SynchronousObjectCache.class);

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            new Thread(() -> {
                try {
                    log.info("1当前对象：{}", JSON.toJSONString(synchronousObjectCache.getValueWithSyncKey("key-one", "syncKey-one")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // log.info("1当前循环：{}", i);
            }).start();
        }

        for (int i = 0; i < 1000; i++) {
            new Thread(() -> {
                try {
                    log.info("2当前对象：{}", JSON.toJSONString(synchronousObjectCache.getValueWithSyncKey("key-two", "syncKey-two")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // log.info("2当前循环：{}", i);
            }).start();
        }
        AtomicLong counter = new AtomicLong(0);
        while (true) {
            log.info("循环获取对象：{}", JSON.toJSONString(synchronousObjectCache.getValueWithSyncKey("key-one", "syncKey-one")));
            commonTestCache.putValue(counter.toString(), counter.toString());
            log.info("向普通本地缓存中添加数据，第{}次，当前缓存数据量：{}", counter, commonTestCache.getSize());
            Thread.sleep(1000);
            counter.incrementAndGet();
        }
    }
}