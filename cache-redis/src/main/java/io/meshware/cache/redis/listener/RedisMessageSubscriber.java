package io.meshware.cache.redis.listener;

import com.alibaba.fastjson.JSON;
import io.meshware.cache.api.event.CacheDiscardEntity;
import io.meshware.cache.api.manager.AbstractCacheSyncManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

/**
 * RedisMessageSubscriber
 *
 * @author Zhiguo.Chen
 */
@Slf4j
public class RedisMessageSubscriber extends AbstractCacheSyncManager implements MessageListener {

    public RedisMessageSubscriber(ApplicationContext applicationContext) {
        super(applicationContext);
    }

    @Override
    public void onMessage(final Message message, final byte[] pattern) {
        log.info("接收到channel:{}, message:{}", new String(message.getChannel(), StandardCharsets.UTF_8), message.toString());
        CacheDiscardEntity cacheDiscard = JSON.parseObject(message.toString(), CacheDiscardEntity.class);
        doCacheDiscard(cacheDiscard);
    }

    /**
     * Init Manager
     *
     * @return
     */
    @Override
    public CompletableFuture<Void> initManager() {
        return CompletableFuture.completedFuture(null);
    }
}
