package io.meshware.cache.api.event;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * CacheDiscard
 *
 * @author Zhiguo.Chen
 */
@Data
@AllArgsConstructor
@Accessors(chain = true)
public class CacheDiscardEntity {

    private String cacheName;

    private Object deleteKey;

    //public Class<T> getCacheClass() {
    //    Class<T> tClass = (Class<T>)((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    //    return tClass;
    //}

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
