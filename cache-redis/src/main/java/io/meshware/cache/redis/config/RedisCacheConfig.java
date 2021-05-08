package io.meshware.cache.redis.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.lang.reflect.Method;

/**
 * Redis Config
 *
 * @author Zhiguo.Chen
 */
@Configuration
@EnableCaching
@AutoConfigureAfter({RedisAutoConfiguration.class})
public class RedisCacheConfig extends CachingConfigurerSupport {

    //@Bean
    //public RedisConnectionFactory defaultLettuceConnectionFactory(RedisStandaloneConfiguration defaultRedisConfig,
    //                                                                GenericObjectPoolConfig defaultPoolConfig) {
    //    LettuceClientConfiguration clientConfig =
    //            LettucePoolingClientConfiguration.builder().commandTimeout(Duration.ofMillis(100))
    //                    .poolConfig(defaultPoolConfig).build();
    //    return new LettuceConnectionFactory(defaultRedisConfig, clientConfig);
    //}

    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... params) {
                StringBuilder sb = new StringBuilder();
                sb.append(target.getClass().getName());
                sb.append(method.getName());
                for (Object obj : params) {
                    sb.append(obj.toString());
                }
                return sb.toString();
            }
        };
    }

//    @SuppressWarnings("rawtypes")
//    @Bean
//    public CacheManager cacheManager(RedisTemplate redisTemplate) {
//        RedisCacheManager rcm = new RedisCacheManager(redisTemplate);
//        //设置缓存过期时间
//        rcm.setDefaultExpiration(60 * 60);//秒
//        return rcm;
//    }

    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
        return RedisCacheManager.create(connectionFactory);
    }

}
