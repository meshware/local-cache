package io.meshware.cache.api.manager;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.meshware.cache.api.event.CacheDiscardEntity;
import io.meshware.cache.api.event.CacheDiscardEvent;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.*;

/**
 * Cache Sync Manager
 *
 * @author Zhiguo.Chen
 */
@Slf4j
@Component
public abstract class AbstractCacheSyncManager implements CacheMessageSubscriber {

    protected static final ExecutorService pool;

    static {
        ThreadFactory nameThreadFactory = new ThreadFactoryBuilder().setNameFormat("cache-sync-pool-%d").build();
        pool = new ThreadPoolExecutor(5, 200, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1024), nameThreadFactory, new ThreadPoolExecutor.AbortPolicy());
    }

    private ApplicationContext applicationContext;

    private AbstractCacheSyncManager() {
    }

    /**
     * Init Manager
     *
     * @return
     */
    @PostConstruct
    public abstract CompletableFuture<Void> initManager();

    @Autowired
    public AbstractCacheSyncManager(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public CompletableFuture<Void> addSubscriber(Runnable runnable) {
        pool.execute(runnable);
        return CompletableFuture.completedFuture(null);
    }

    public CompletableFuture<Void> doCacheDiscard(@NonNull CacheDiscardEntity cacheDiscard) {
        applicationContext.publishEvent(new CacheDiscardEvent(cacheDiscard));
        return CompletableFuture.completedFuture(null);
    }
}
