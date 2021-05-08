package io.meshware.cache.ohc.serializer.protostuff;

import com.esotericsoftware.kryo.io.ByteBufferInputStream;
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

    // protected final Type type = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];

    public ProtostuffObjectSerializer(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public void serialize(T value, ByteBuffer buf) {
        // WritableByteChannel channel = Channels.newChannel(stream);
        // channel.write(buf);
        // serialization.getSerializer().serialize(new ByteBufferOutputStream(buf), value);
        if (null == value) {
            return;
        }
        byte[] bytes = ProtostuffSerializationUtils.serializer(value);
        if (null != bytes && bytes.length > 0) {
            buf.put(bytes);
        }
    }

    @Override
    public T deserialize(ByteBuffer buf) {
        // return serialization.getSerializer().deserialize(new ByteBufferInputStream(buf), type);
        try {
            return ProtostuffSerializationUtils.deserializer(new ByteBufferInputStream(buf), clazz);
        } catch (Exception e) {
            log.error("Deserialize by protostuff error! message={}", e.getMessage(), e);
        }
        return null;
    }

    @Override
    public int serializedSize(T value) {
        //TODO 用JDK序列化结果判断最大自己长度，非最佳实践
        // byte[] bytes = SerializationUtils.serialize(value);
        // ByteBuffer byteBuffer = ByteBuffer.allocate(bytes == null ? 0 : bytes.length);
        // serialization.getSerializer().serialize(new ByteBufferOutputStream(byteBuffer), value);
        // return byteBuffer.position();
        byte[] bytes = ProtostuffSerializationUtils.serializer(value);
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
