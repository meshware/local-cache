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
package io.meshware.cache.spring;

import io.meshware.cache.ihc.AbstractCommonCache;
import io.meshware.cache.spring.event.CacheDiscardEntity;
import io.meshware.cache.spring.event.CacheDiscardEvent;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.event.EventListener;

/**
 * Abstract Common Cache
 *
 * @author Zhiguo.Chen
 */
@Slf4j
@Data
@Accessors(chain = true)
public abstract class AbstractEventCommonCache<K, V> extends AbstractCommonCache<K, V> implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        initConfig();
        init();
    }

    @EventListener
    public void discardCacheByKey(CacheDiscardEvent cacheDiscardEvent) {
        CacheDiscardEntity cacheDiscard = (CacheDiscardEntity) cacheDiscardEvent.getSource();
        if (log.isDebugEnabled()) {
            log.debug("Receive event:{}, deleteKey:{}, current cache:{}", cacheDiscard.getClass(), cacheDiscard.getDeleteKey(), this.getClass().getSimpleName());
        }
        if (this.getName().equals(cacheDiscard.getCacheName())) {
            removeValue((K) cacheDiscard.getDeleteKey());
        }
    }
}
