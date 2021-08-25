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
package io.meshware.cache.api;

import java.util.Collection;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Local cache interface
 *
 * @author Zhiguo.Chen
 */
public interface LocalCache<K, V> extends Cache {

    /**
     * Get value by key
     *
     * @param key key
     * @return V
     */
    V getValue(K key) throws Exception;

    /**
     * Get value and return default value if not exist
     *
     * @param key          key
     * @param defaultValue default value
     * @return V
     */
    V getValueOrDefault(K key, V defaultValue);

    /**
     * Get value and return default value if not exist
     *
     * @param key                  key
     * @param defaultValueSupplier default value supplier
     * @return V
     */
    V getValueOrSupplier(K key, Supplier<V> defaultValueSupplier);

    void putValue(K key, V value);

    void removeValue(K key);

    void removeAll();

    void cleanUp();

    Set<K> getKeys();

    Collection<V> getValues();

    long getMaxSize();

    long getSize();

    boolean containsKey(K key);
}
