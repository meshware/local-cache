package io.meshware.cache.redis.implement;

import io.meshware.cache.api.RedisCache;
import io.meshware.cache.api.properties.CacheAdapterProperties;
import io.meshware.cache.redis.publisher.RedisMessagePublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis Client Adaptor
 *
 * @author Zhiguo.Chen
 */
public class RedisClient implements RedisCache {

    private final StringRedisTemplate stringRedisTemplate;

    private final CacheAdapterProperties cacheAdapterProperties;

    private final RedisMessagePublisher redisMessagePublisher;

    public RedisClient(StringRedisTemplate stringRedisTemplate,
                       CacheAdapterProperties cacheAdapterProperties,
                       RedisMessagePublisher redisMessagePublisher) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.cacheAdapterProperties = cacheAdapterProperties;
        this.redisMessagePublisher = redisMessagePublisher;
    }

    @Override
    public void del(String key) {
        stringRedisTemplate.delete(key);
    }

    @Override
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    @Override
    public void set(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    @Override
    public boolean setnx(String key, String value) {
        return stringRedisTemplate.opsForValue().setIfAbsent(key, value);
    }

    @Override
    public boolean setnx(String key, String value, long time) {
        return stringRedisTemplate.opsForValue().setIfAbsent(key, value, time, TimeUnit.SECONDS);
    }

    @Override
    public void setWithNotify(String key, String value, String cacheName, String cacheKey) {
        set(key, value);
        if (StringUtils.hasText(cacheAdapterProperties.getDiscardChannel())) {
            redisMessagePublisher.sendDiscardCacheMessage(cacheAdapterProperties.getDiscardChannel(), cacheName, cacheKey);
        }
    }

    @Override
    public void hmset(String key, Map value) {
        stringRedisTemplate.opsForHash().putAll(key, value);
    }

    @Override
    public void hset(String key, String k, String v) {
        stringRedisTemplate.opsForHash().put(key, k, v);
    }

    @Override
    public String hget(String key, String k) {
        return (String) stringRedisTemplate.opsForHash().get(key, k);
    }

    @Override
    public Map hgetAll(String key) {
        return stringRedisTemplate.opsForHash().entries(key);
    }

    @Override
    public Long hdel(String key, String value) {
        return stringRedisTemplate.opsForHash().delete(key, value);
    }

    @Override
    public boolean exists(String key) {
        return stringRedisTemplate.hasKey(key);
    }

    @Override
    public Boolean sismember(String key, String k) {
        return stringRedisTemplate.opsForSet().isMember(key, k);
    }

    @Override
    public Set<String> smembers(String key) {
        return stringRedisTemplate.opsForSet().members(key);
    }

    @Override
    public Long sadd(String key, String value) {
        return stringRedisTemplate.opsForSet().add(key, value);
    }

    @Override
    public void srem(String key, String k) {
        stringRedisTemplate.opsForSet().remove(key, k);
    }

    @Override
    public void lpush(String key, String value) {
        stringRedisTemplate.opsForList().leftPush(key, value);
    }

    @Override
    public void lpush(String key, String[] value) {
        stringRedisTemplate.opsForList().leftPushAll(key, value);
    }

    @Override
    public String ltrim(String key, long start, long end) {
        stringRedisTemplate.opsForList().trim(key, start, end);
        return "OK";
    }

    @Override
    public List<String> lrange(String key, long start, long end) {
        return stringRedisTemplate.opsForList().range(key, start, end);
    }

    @Override
    public Long llen(String key) {
        return stringRedisTemplate.opsForList().size(key);
    }

    @Override
    public String lpop(String key) {
        return stringRedisTemplate.opsForList().leftPop(key);
    }

    @Override
    public String rpop(String key) {
        return stringRedisTemplate.opsForList().rightPop(key);
    }

    @Override
    public Boolean expire(String key, long maxTime) {
        return stringRedisTemplate.expire(key, maxTime, TimeUnit.SECONDS);
    }

    @Override
    public Boolean zadd(String key, double score, String value) {
        return stringRedisTemplate.opsForZSet().add(key, value, score);
    }

    @Override
    public Long zrem(String key, String value) {
        return stringRedisTemplate.opsForZSet().remove(key, value);
    }

    @Override
    public Long zrank(String key, String value) {
        return stringRedisTemplate.opsForZSet().rank(key, value);
    }

    @Override
    public Set<String> zrange(String key, long start, long end) {
        return stringRedisTemplate.opsForZSet().range(key, start, end);
    }

    @Override
    public void setex(String key, long time, String value) {
        stringRedisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
    }

    @Override
    public void publish(String channel, Object value) {
        stringRedisTemplate.convertAndSend(channel, value);
    }

    @Override
    public Long lrem(String key, long count, String value) {
        return stringRedisTemplate.opsForList().remove(key, count, value);
    }

    @Override
    public String lindex(String key, long index) {
        return stringRedisTemplate.opsForList().index(key, index);
    }

    @Override
    public Long rpush(String key, String... value) {
        return stringRedisTemplate.opsForList().rightPushAll(key, value);
    }

    @Override
    public Set<String> zrevrangeByScore(String key, double max, double min, int offset, int count) {
        return stringRedisTemplate.opsForZSet().reverseRangeByScore(key, max, min, offset, count);
    }

    @Override
    public Long zrevrank(String key, String member) {
        return stringRedisTemplate.opsForZSet().reverseRank(key, member);
    }

    @Override
    public Long incr(String key) {
        return stringRedisTemplate.boundValueOps(key).increment();
    }

    @Override
    public Boolean setBit(String key, long offset, boolean value) {
        return stringRedisTemplate.opsForValue().setBit(key, offset, value);
    }

    @Override
    public Boolean getBit(String key, long offset) {
        return stringRedisTemplate.opsForValue().getBit(key, offset);
    }
}
