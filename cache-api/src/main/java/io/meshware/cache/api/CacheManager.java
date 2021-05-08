package io.meshware.cache.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache manager
 *
 * @author Zhiguo.Chen
 */
@Slf4j
public class CacheManager {

    private static ApplicationContext applicationContext;

    private static Map<String, Cache> cacheMap = new ConcurrentHashMap<>();

    @Autowired
    public CacheManager(ApplicationContext applicationContext) {
        CacheManager.applicationContext = applicationContext;
        init();
    }

    public static void init() {
        if (applicationContext != null) {
            Map<String, Cache> beans = applicationContext.getBeansOfType(Cache.class);
            beans.forEach((beanName, cacheBean) -> addHandler(cacheBean, beanName));
            log.info("Init local cache finished, local cache count:{}", cacheMap.size());
        } else {
            log.error("ApplicationContext is null, please value ascribed first!");
        }
    }

    public static void addHandler(Cache cache, String defaultName) {
        String cacheName = StringUtils.hasText(cache.getName()) ? defaultName : cache.getName();
        if (!cacheMap.containsKey(cacheName)) {
            cacheMap.putIfAbsent(cacheName, cache);
        } else {
            log.error("[######]CacheName[{}] has duplicate cache bean. This cache will be ignored (invalid)! handler class={}",
                    cacheName, cache.getClass().getCanonicalName());
        }
    }

    public static Cache getCache(String cacheName) {
        if (cacheMap.size() == 0) {
            init();
        }
        return cacheMap.get(cacheName);
    }
}
