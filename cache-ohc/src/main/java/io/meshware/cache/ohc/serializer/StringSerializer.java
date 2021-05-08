package io.meshware.cache.ohc.serializer;

import com.google.common.base.Charsets;
import org.caffinitas.ohc.CacheSerializer;

import java.nio.ByteBuffer;

/**
 * String Serializer
 *
 * @author Zhiguo.Chen
 * @version 20210210
 */
public class StringSerializer implements CacheSerializer<String> {

    @Override
    public void serialize(String s, ByteBuffer byteBuffer) {
        byte[] bytes = s.getBytes(Charsets.UTF_8);
        byteBuffer.put((byte) ((bytes.length >>> 8) & 0xFF));
        byteBuffer.put((byte) ((bytes.length >>> 0) & 0xFF));
        byteBuffer.put(bytes);
    }

    @Override
    public String deserialize(ByteBuffer byteBuffer) {
        int length = (((byteBuffer.get() & 0xff) << 8) + ((byteBuffer.get() & 0xff) << 0));
        byte[] bytes = new byte[length];
        byteBuffer.get(bytes);
        return new String(bytes, Charsets.UTF_8);
    }

    @Override
    public int serializedSize(String s) {
        return writeUTFLen(s);
    }

    static int writeUTFLen(String str) {
        int strlen = str.length();
        int utflen = 0;
        int c;

        for (int i = 0; i < strlen; i++) {
            c = str.charAt(i);
            if ((c >= 0x0001) && (c <= 0x007F))
                utflen++;
            else if (c > 0x07FF)
                utflen += 3;
            else
                utflen += 2;
        }

        if (utflen > 65535)
            throw new RuntimeException("encoded string too long: " + utflen + " bytes");

        return utflen + 2;
    }
}