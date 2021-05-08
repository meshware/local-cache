package io.meshware.cache.ohc.serializer;

import org.caffinitas.ohc.CacheSerializer;

import java.nio.ByteBuffer;

/**
 * Integer Key Serializer
 *
 * @author Zhiguo.Chen
 * @version 20210210
 */
public class IntegerSerializer implements CacheSerializer<Integer> {

    public static final int FIXED_KEY_LEN = 68;

    @Override
    public  void serialize(Integer integer, ByteBuffer byteBuffer) {
        byteBuffer.putInt(integer);
        for (int i = 4; i < FIXED_KEY_LEN; i++) {
            byteBuffer.put((byte) 0);
        }
    }

    @Override
    public Integer deserialize(ByteBuffer byteBuffer) {
        return byteBuffer.getInt();
    }

    @Override
    public int serializedSize(Integer integer) {
        return FIXED_KEY_LEN;
    }
}