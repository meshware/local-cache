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

/**
 * Redis key-value operations interface.
 * <p>
 * Defines basic Redis string/key operations including get, set, delete,
 * expiration, publish, increment, and bitmap operations.
 * </p>
 *
 * @author Zhiguo.Chen
 */
public interface RedisKeyValueOperations {

    void del(String key);

    String get(String key);

    void set(String key, String value);

    boolean setnx(String key, String value);

    boolean setnx(String key, String value, long time);

    void setex(String key, long time, String value);

    boolean exists(String key);

    Boolean expire(String key, long maxTime);

    Long incr(String key);

    void publish(String channel, Object value);

    Boolean setBit(String key, long offset, boolean value);

    Boolean getBit(String key, long offset);
}
