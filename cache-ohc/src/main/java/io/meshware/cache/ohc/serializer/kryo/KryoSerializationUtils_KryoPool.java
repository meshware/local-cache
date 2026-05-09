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

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoPool;
import org.springframework.lang.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class KryoSerializationUtils_KryoPool<T> {

    /**
     * KryoPool with shared Kryo factory configuration
     */
    private static final KryoPool kryoPool;

    static {
        kryoPool = new KryoPool.Builder(KryoFactory::createKryo).softReferences().build();
    }

    /**
     * serialize
     *
     * @param object object
     * @return byte[]
     */
    @Nullable
    public static byte[] serialize(@Nullable Object object) {
        if (object == null) {
            return null;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
        Kryo kryo = kryoPool.borrow();
        try (Output output = new Output(byteArrayOutputStream)) {
            kryo.writeObject(output, object);
            output.flush();
        } catch (KryoException ke) {
            throw new IllegalStateException("Failed to serialize object", ke);
        } finally {
            kryoPool.release(kryo);
        }
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * deserialize
     *
     * @param bytes byte[]
     * @param clazz Class
     * @param <T>   T
     * @return T
     */
    @Nullable
    public static <T> T deserialize(@Nullable byte[] bytes, Class<T> clazz) {
        if (bytes == null) {
            return null;
        }
        Kryo kryo = kryoPool.borrow();
        try (Input input = new Input(new ByteArrayInputStream(bytes))) {
            return kryo.readObject(input, clazz);
        } catch (KryoException ke) {
            throw new IllegalStateException("Failed to deserialize object", ke);
        } finally {
            kryoPool.release(kryo);
        }
    }
}
