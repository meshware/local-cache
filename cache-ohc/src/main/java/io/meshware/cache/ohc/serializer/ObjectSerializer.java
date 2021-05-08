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

    @Override
    public void serialize(T t, ByteBuffer byteBuffer) {
        Assert.notNull(t, "传入对象不可为null！");
        byte[] bytes = SerializationUtils.serialize(t);
        byteBuffer.put(bytes);
    }

    @Override
    public T deserialize(ByteBuffer byteBuffer) {
        byte[] bytes = new byte[byteBuffer.capacity()];
        byteBuffer.get(bytes);
        return (T) SerializationUtils.deserialize(bytes);
    }

    @Override
    public int serializedSize(T t) {
        byte[] bytes = SerializationUtils.serialize(t);
        return bytes == null ? 0 : bytes.length;
    }
}
