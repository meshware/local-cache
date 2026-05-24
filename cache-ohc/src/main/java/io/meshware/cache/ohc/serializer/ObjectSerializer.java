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
package io.meshware.cache.ohc.serializer;

import lombok.extern.slf4j.Slf4j;
import org.caffinitas.ohc.CacheSerializer;
import org.springframework.util.Assert;
import org.springframework.util.SerializationUtils;

import java.nio.ByteBuffer;

/**
 * ObjectSerializer
 *
 * @author Zhiguo.Chen
 * @version 20210219
 */
@Slf4j
public class ObjectSerializer<T> implements CacheSerializer<T> {

    /**
     * Cache serialized bytes to avoid double serialization.
     * OHC calls serializedSize() first, then serialize(), so we can reuse the result.
     */
    private final ThreadLocal<byte[]> cachedBytes = new ThreadLocal<>();

    @Override
    public void serialize(T t, ByteBuffer byteBuffer) {
        Assert.notNull(t, "Object to serialize must not be null!");
        byte[] bytes = cachedBytes.get();
        if (bytes == null) {
            bytes = SerializationUtils.serialize(t);
        } else {
            cachedBytes.remove();
        }
        byteBuffer.put(bytes);
    }

    @Override
    public T deserialize(ByteBuffer byteBuffer) {
        byte[] bytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(bytes);
        return (T) SerializationUtils.deserialize(bytes);
    }

    @Override
    public int serializedSize(T t) {
        byte[] bytes = SerializationUtils.serialize(t);
        cachedBytes.set(bytes);
        return bytes == null ? 0 : bytes.length;
    }
}
