package io.meshware.cache.ohc.serializer.protostuff;

import com.esotericsoftware.kryo.io.ByteBufferInputStream;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.DefaultIdStrategy;
import io.protostuff.runtime.IdStrategy;
import io.protostuff.runtime.RuntimeSchema;
import lombok.extern.slf4j.Slf4j;

import java.sql.Date;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * ProtostuffSerializationUtil
 *
 * @author Zhiguo.Chen
 * @version 20210416
 */
@Slf4j
public class ProtostuffSerializationUtils {

    protected static ThreadLocal<LinkedBuffer> local = ThreadLocal.withInitial(() -> LinkedBuffer.allocate(1024));

    // private static final Map<Class<?>,Schema<?>> SCHEMA_MAP = new ConcurrentHashMap<>();

    protected static final DefaultIdStrategy STRATEGY = new DefaultIdStrategy(IdStrategy.DEFAULT_FLAGS |
            IdStrategy.ALLOW_NULL_ARRAY_ELEMENT);

    private static final Class<SerializeDeserializeWrapper> SERIALIZE_DESERIALIZE_WRAPPER_OBJ_CLASS =
            SerializeDeserializeWrapper.class;

    private static final Schema<SerializeDeserializeWrapper> WRAPPER_SCHEMA =
            RuntimeSchema.createFrom(SERIALIZE_DESERIALIZE_WRAPPER_OBJ_CLASS);

    private static final Set<Class<?>> WRAPPER_CLASS_SET = new HashSet<>();

    static {
        WRAPPER_CLASS_SET.add(List.class);
        WRAPPER_CLASS_SET.add(ArrayList.class);
        WRAPPER_CLASS_SET.add(LinkedList.class);
        WRAPPER_CLASS_SET.add(CopyOnWriteArrayList.class);
        WRAPPER_CLASS_SET.add(Set.class);
        WRAPPER_CLASS_SET.add(HashSet.class);
        WRAPPER_CLASS_SET.add(LinkedHashSet.class);
        WRAPPER_CLASS_SET.add(CopyOnWriteArraySet.class);
        WRAPPER_CLASS_SET.add(Map.class);
        WRAPPER_CLASS_SET.add(HashMap.class);
        WRAPPER_CLASS_SET.add(ConcurrentHashMap.class);
        WRAPPER_CLASS_SET.add(LinkedHashMap.class);
        WRAPPER_CLASS_SET.add(Date.class);
    }

    private ProtostuffSerializationUtils() {
    }

    public static <T> byte[] serializer(T o) {
        LinkedBuffer linkedBuffer = local.get();
        try {
            if (WRAPPER_CLASS_SET.contains(o.getClass())) {
                return ProtostuffIOUtil.toByteArray(SerializeDeserializeWrapper.builder(o), WRAPPER_SCHEMA, linkedBuffer);
            } else {
                return ProtostuffIOUtil.toByteArray(o, (Schema<T>) RuntimeSchema.getSchema(o.getClass(), STRATEGY), linkedBuffer);
            }
        } finally {
            linkedBuffer.clear();
        }
    }

    // public static <T> Schema getSchema(Class<T> clazz){
    //     if(SCHEMA_MAP.containsKey(clazz)){
    //         return SCHEMA_MAP.get(clazz);
    //     }else{
    //         Schema<T> schema = RuntimeSchema.createFrom(clazz);
    //         SCHEMA_MAP.put(clazz,schema);
    //         return schema;
    //     }
    // }

    public static <T> T deserializer(ByteBufferInputStream byteBufferInputStream, Class<T> clazz) throws Exception {
        LinkedBuffer linkedBuffer = local.get();
        try {
            if (WRAPPER_CLASS_SET.contains(clazz)) {
                SerializeDeserializeWrapper<T> obj = new SerializeDeserializeWrapper<>();
                ProtostuffIOUtil.mergeFrom(byteBufferInputStream, obj, WRAPPER_SCHEMA, linkedBuffer);
                return obj.getData();
            } else {
                Schema<T> schema = RuntimeSchema.getSchema(clazz, STRATEGY);
                T obj = schema.newMessage();
                // T obj = clazz.newInstance();
                ProtostuffIOUtil.mergeFrom(byteBufferInputStream, obj, schema);
                return obj;
            }
        } finally {
            linkedBuffer.clear();
        }
    }

    private static class SerializeDeserializeWrapper<T> {

        private T data;

        public static <T> SerializeDeserializeWrapper<T> builder(T data) {
            SerializeDeserializeWrapper<T> wrapper = new SerializeDeserializeWrapper<>();
            wrapper.setData(data);
            return wrapper;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }
    }


}
