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
package io.meshware.cache.ohc.serializer.protostuff;

import io.meshware.cache.ohc.serializer.protostuff.ByteBufferInputStream;
import lombok.extern.slf4j.Slf4j;
import org.caffinitas.ohc.CacheSerializer;

import java.lang.reflect.ParameterizedType;
import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * ProtostuffObjectSerializer
 *
 * @author Zhiguo.Chen
 * @version 20210415
 */
@Slf4j
public class ProtostuffObjectSerializer<T> implements CacheSerializer<T> {

    private Class<T> clazz;
    private final ThreadLocal<byte[]> cachedBytes = new ThreadLocal<>();

    public ProtostuffObjectSerializer(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public void serialize(T value, ByteBuffer buf) {
        if (null == value) {
            return;
        }
        byte[] bytes = cachedBytes.get();
        if (bytes == null) {
            bytes = ProtostuffSerializationUtils.serializer(value);
        } else {
            cachedBytes.remove();
        }
        if (null != bytes && bytes.length > 0) {
            buf.put(bytes);
        }
    }

    @Override
    public T deserialize(ByteBuffer buf) {
        try {
            return ProtostuffSerializationUtils.deserializer(new ByteBufferInputStream(buf), clazz);
        } catch (Exception e) {
            log.error("Deserialize by protostuff error! message={}", e.getMessage(), e);
        }
        return null;
    }

    @Override
    public int serializedSize(T value) {
        byte[] bytes = ProtostuffSerializationUtils.serializer(value);
        cachedBytes.set(bytes);
        return Objects.nonNull(bytes) ? bytes.length : 0;
    }

    public Class<T> getGenericClass() {
        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        return (Class<T>) parameterizedType.getActualTypeArguments()[0];
    }

    // static class ByteBufferOutputStream extends OutputStream {
    //
    //     protected ByteBuffer buffer;
    //
    //     public ByteBufferOutputStream(ByteBuffer buffer) {
    //         this.buffer = buffer;
    //     }
    //
    //     @Override
    //     public void write(final int b) {
    //         buffer.putInt(b);
    //     }
    //
    //     @Override
    //     public void write(final byte[] bytes) {
    //         buffer.put(bytes);
    //     }
    //
    //     @Override
    //     public void write(final byte[] bytes, final int offset, final int length) {
    //         if (length <= 0) {
    //             return;
    //         }
    //         buffer.put(bytes, offset, length);
    //     }
    //
    // }
    //
    // static class ByteBufferInputStream extends InputStream {
    //     //缓冲区
    //     protected ByteBuffer buffer;
    //     //最大位置
    //     protected int endIndex;
    //
    //     public ByteBufferInputStream(ByteBuffer buffer) {
    //         this(buffer, buffer.limit());
    //     }
    //
    //     public ByteBufferInputStream(ByteBuffer buffer, int length) {
    //         if (length < 0) {
    //             throw new IllegalArgumentException("length: " + length);
    //         }
    //         if (length > buffer.capacity()) {
    //             throw new IndexOutOfBoundsException();
    //         }
    //         this.buffer = buffer;
    //         this.endIndex = buffer.position() + length;
    //     }
    //
    //     @Override
    //     public int read() {
    //         return buffer.hasRemaining() ? buffer.get() & 0xff : -1;
    //     }
    //
    //     @Override
    //     public int read(final byte[] b, final int off, final int len) {
    //         int available = available();
    //         if (available <= 0) {
    //             return -1;
    //         }
    //         int length = Math.min(available, len);
    //         buffer.get(b, off, length);
    //         return length;
    //     }
    //
    //     @Override
    //     public int available() {
    //         return endIndex - buffer.position();
    //     }
    //
    // }

}
