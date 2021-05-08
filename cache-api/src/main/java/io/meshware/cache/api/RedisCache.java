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
package io.meshware.cache.api;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Redis Cache Interface
 *
 * @author Zhiguo.Chen
 */
public interface RedisCache extends Cache {

    void del(String key);

    String get(String key);

    void set(String key, String value);

    boolean setnx(final String key, final String value);

    boolean setnx(final String key, final String value, final long time);

    void setWithNotify(String key, String value, String cacheName, String cacheKey);

    void hmset(String key, Map value);

    void hset(String key, String k, String v);

    String hget(String key, String k);

    Map hgetAll(String key);

    Long hdel(String key, String value);

    boolean exists(String key);

    Boolean sismember(String key, String k);

    Set<String> smembers(String key);

    Long sadd(String key, String value);

    void srem(String key, String k);

    void lpush(String key, String value);

    void lpush(String key, String[] value);

    String ltrim(String key, long start, long end);

    List<String> lrange(String key, long start, long end);

    Long llen(String key);

    String lpop(String key);

    Long lrem(String key, long count, String value);

    String lindex(String key, long index);

    String rpop(String key);

    Long rpush(String key, String... value);

    Boolean expire(String key, long maxTime);

    Boolean zadd(String key, double score, String value);

    Long zrem(String key, String value);

    Long zrank(String key, String value);

    Set<String> zrange(String key, long start, long end);

    void setex(String key, long time, String value);

    void publish(String channel, Object value);

    Set<String> zrevrangeByScore(final String key, final double max, final double min, final int offset, final int count);

    Long zrevrank(final String key, final String member);

    Long incr(final String key);

    Boolean setBit(String key, long offset, boolean value);

    Boolean getBit(String key, long offset);
}
