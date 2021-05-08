import com.github.benmanes.caffeine.cache.Cache;
import io.meshware.cache.ihc.AbstractCommonCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Description
 *
 * @author Zhiguo.Chen
 */
@Slf4j
@Component
public class CommonTestCache extends AbstractCommonCache<String, String> {

    /**
     * Set a name for the cache
     *
     * @return
     */
    @Override
    public String getName() {
        return "commonTestCache";
    }

    /**
     * Init cache config
     */
    @Override
    public void initConfig() {
        this.setExpireDurationAfterWrite(3);
        this.setExpireTimeUnit(TimeUnit.SECONDS);
    }

    /**
     * Init cache
     *
     * @param cache
     */
    @Override
    public void initCache(Cache<String, String> cache) {
        cache.put("aaa", "ooo");
    }

    public static void main(String[] args) {
        CommonTestCache commonTestCache = new CommonTestCache();
        System.out.println(commonTestCache.getValue("bbb"));
        System.out.println(commonTestCache.getValue("aaa"));
    }
}
