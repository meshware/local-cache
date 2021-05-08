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
public class KryoObjectSerializer<T> implements CacheSerializer<T> {

    private Class<T> clazz;

    public KryoObjectSerializer(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public void serialize(T t, ByteBuffer byteBuffer) {
        Assert.notNull(t, "传入对象不可为null！");
        byte[] bytes = KryoSerializationUtils.serialize(t);
        byteBuffer.put(bytes);
    }

    @Override
    public T deserialize(ByteBuffer byteBuffer) {
        byte[] bytes = new byte[byteBuffer.capacity()];
        byteBuffer.get(bytes);
        return KryoSerializationUtils.deserialize(bytes, clazz);
    }

    @Override
    public int serializedSize(T t) {
        byte[] bytes = KryoSerializationUtils.serialize(t);
        return bytes == null ? 0 : bytes.length;
    }
}
