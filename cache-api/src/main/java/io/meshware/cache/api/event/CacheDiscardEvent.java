package io.meshware.cache.api.event;

import org.springframework.context.ApplicationEvent;

/**
 * Cache Discard Event
 *
 * @author Zhiguo.Chen
 */
public class CacheDiscardEvent extends ApplicationEvent {

    public CacheDiscardEvent(CacheDiscardEntity source) {
        super(source);
    }

    //private String aaa;
}
