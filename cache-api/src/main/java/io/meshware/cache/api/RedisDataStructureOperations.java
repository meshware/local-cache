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
 * Redis data structure operations interface.
 * <p>
 * Defines operations for Hash, List, Set, and Sorted Set data structures.
 * </p>
 *
 * @author Zhiguo.Chen
 */
public interface RedisDataStructureOperations {

    // ---- Hash operations ----

    void hmset(String key, Map<String, String> value);

    void hset(String key, String k, String v);

    String hget(String key, String k);

    Map<String, String> hgetAll(String key);

    Long hdel(String key, String value);

    // ---- List operations ----

    void lpush(String key, String value);

    void lpush(String key, String[] value);

    String ltrim(String key, long start, long end);

    List<String> lrange(String key, long start, long end);

    Long llen(String key);

    String lpop(String key);

    String rpop(String key);

    Long lrem(String key, long count, String value);

    String lindex(String key, long index);

    Long rpush(String key, String... value);

    // ---- Set operations ----

    Boolean sismember(String key, String k);

    Set<String> smembers(String key);

    Long sadd(String key, String value);

    void srem(String key, String k);

    // ---- Sorted Set operations ----

    Boolean zadd(String key, double score, String value);

    Long zrem(String key, String value);

    Long zrank(String key, String value);

    Set<String> zrange(String key, long start, long end);

    Set<String> zrevrangeByScore(String key, double max, double min, int offset, int count);

    Long zrevrank(String key, String member);
}
