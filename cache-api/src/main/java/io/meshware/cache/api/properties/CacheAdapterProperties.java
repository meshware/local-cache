package io.meshware.cache.api.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Cache Adapter Properties
 *
 * @author Zhiguo.Chen
 */
@Data
@Validated
@ConfigurationProperties(prefix = "cache.adapter")
public class CacheAdapterProperties {

    private boolean enable = true;

    private String cacheType;

    private String discardChannel;

}
