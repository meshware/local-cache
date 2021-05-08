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
package io.meshware.cache.ohc.serializer.kryo;

import lombok.extern.slf4j.Slf4j;
import org.caffinitas.ohc.CacheSerializer;
import org.springframework.util.Assert;

import java.nio.ByteBuffer;

/**
 * Kryo object serializer
 *
 * @author Zhiguo.Chen
 * @version 20210219
 */
@Slf4j
public class KryoObjectSerializer_KryoPool<T> implements CacheSerializer<T> {

    private Class<T> clazz;

    public KryoObjectSerializer_KryoPool(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public void serialize(T t, ByteBuffer byteBuffer) {
        Assert.notNull(t, "传入对象不可为null！");
        byte[] bytes = KryoSerializationUtils_KryoPool.serialize(t);
        byteBuffer.put(bytes);
    }

    @Override
    public T deserialize(ByteBuffer byteBuffer) {
        byte[] bytes = new byte[byteBuffer.capacity()];
        byteBuffer.get(bytes);
        return KryoSerializationUtils_KryoPool.deserialize(bytes, clazz);
    }

    @Override
    public int serializedSize(T t) {
        byte[] bytes = KryoSerializationUtils_KryoPool.serialize(t);
        return bytes == null ? 0 : bytes.length;
    }
}
