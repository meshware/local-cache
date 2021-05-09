package io.meshware.cache.sample.springboot;

import io.meshware.cache.sample.springboot.cache.CommonTestCache;
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

        CommonTestCache commonTestCache = run.getBean(CommonTestCache.class);

        AtomicLong counter = new AtomicLong(0);
        while (true) {
            commonTestCache.putValue(counter.toString(), counter.toString());
            log.info("向普通本地缓存中添加数据，第{}次，当前缓存数据量：{}", counter, commonTestCache.getSize());
            Thread.sleep(1000);
            counter.incrementAndGet();
        }
    }
}