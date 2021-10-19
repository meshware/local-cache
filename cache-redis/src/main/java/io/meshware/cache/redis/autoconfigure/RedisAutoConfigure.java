/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.meshware.cache.redis.autoconfigure;

import io.meshware.cache.api.Cache;
import io.meshware.cache.api.RedisCache;
import io.meshware.cache.redis.config.RedisCacheConfig;
import io.meshware.cache.redis.implement.RedisClient;
import io.meshware.cache.redis.listener.RedisMessageSubscriber;
import io.meshware.cache.redis.publisher.RedisMessagePublisher;
import io.meshware.cache.spring.manager.CacheManager;
import io.meshware.cache.spring.properties.CacheAdapterProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * Redis AutoConfigure
 *
 * @author Zhiguo.Chen
 */
@Slf4j
@ConditionalOnClass(Cache.class)
@EnableAspectJAutoProxy(proxyTargetClass = true)
@AutoConfigureBefore({RedisAutoConfiguration.class})
@EnableConfigurationProperties(CacheAdapterProperties.class)
@Import({RedisCacheConfig.class})
@ConditionalOnExpression(value = "${cache.adapter.enable:true} and '${cache.adapter.cache-type}' eq '" + RedisAutoConfigure.CACHE_TYPE + "'")
public class RedisAutoConfigure {

    public static final String CACHE_TYPE = "redis";

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        return new StringRedisTemplate(redisConnectionFactory);
    }

    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate template = new RedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(RedisSerializer.string());
        template.setValueSerializer(RedisSerializer.json());
        template.setHashKeySerializer(RedisSerializer.string());
        template.setHashValueSerializer(RedisSerializer.json());
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    @ConditionalOnBean(StringRedisTemplate.class)
    @ConditionalOnMissingBean(RedisCache.class)
    @DependsOn({"stringRedisTemplate", "redisMessagePublisher"})
    @ConditionalOnProperty(prefix = "cache.adapter", value = "enable", havingValue = "true")
    public RedisCache redisCache(CacheAdapterProperties cacheAdapterProperties, StringRedisTemplate stringRedisTemplate,
                                 RedisMessagePublisher redisMessagePublisher) {
        if (CACHE_TYPE.equalsIgnoreCase(cacheAdapterProperties.getCacheType())) {
            return new RedisClient(stringRedisTemplate, cacheAdapterProperties, redisMessagePublisher);
        } else {
            log.error("CacheType is not 'redis', create bean error!");
            throw new IllegalArgumentException("CacheType setting is not 'redis'!");
        }
    }

    @Bean
    @ConditionalOnBean(StringRedisTemplate.class)
    @DependsOn("stringRedisTemplate")
    public RedisMessagePublisher redisMessagePublisher(StringRedisTemplate stringRedisTemplate) {
        return new RedisMessagePublisher(stringRedisTemplate);
    }

    @Bean
    public MessageListenerAdapter messageListener(ApplicationContext applicationContext) {
        return new MessageListenerAdapter(new RedisMessageSubscriber(applicationContext));
    }

    @Bean
    @ConditionalOnExpression(value = "'${cache.adapter.discard-channel:}' ne ''")
    public ChannelTopic topic(CacheAdapterProperties cacheAdapterProperties) {
        return new ChannelTopic(cacheAdapterProperties.getDiscardChannel());
    }

    @Bean
    @DependsOn({"messageListener", "topic"})
    @ConditionalOnBean(ChannelTopic.class)
    public RedisMessageListenerContainer redisContainer(RedisConnectionFactory redisConnectionFactory, MessageListenerAdapter messageListener, ChannelTopic topic) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener(messageListener, topic);
        return container;
    }

    @Bean
    public CacheManager cacheManager(ApplicationContext applicationContext) {
        CacheManager cacheManager = new CacheManager(applicationContext);
        return cacheManager;
    }
}
