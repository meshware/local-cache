import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * OffHeapCacheTest
 *
 * @author Zhiguo.Chen
 * @version 20210219
 */
@Slf4j
public class OffHeapCacheTest {

    private static StringOffHeapCache stringOffHeapCache = new StringOffHeapCache();
    private static ObjectOffHeapCache objectOffHeapCache = new ObjectOffHeapCache();
    private static ListOffHeapCache listOffHeapCache = new ListOffHeapCache();


    @BeforeAll
    public static void init() throws Exception {
        stringOffHeapCache.afterPropertiesSet();
        objectOffHeapCache.afterPropertiesSet();
        listOffHeapCache.afterPropertiesSet();
    }

    @Test
    public void testStringOffHeapCache() throws Exception {
        stringOffHeapCache.put("aaa", "aaa");
        Assert.assertEquals("aaa", stringOffHeapCache.getValue("aaa"));
        stringOffHeapCache.put("aaa", "bbb");
        Assert.assertEquals("bbb", stringOffHeapCache.getValue("aaa"));
        stringOffHeapCache.putValue("ccc", "ccc", 100);
        Assert.assertEquals("ccc", stringOffHeapCache.getValue("ccc"));
        Thread.sleep(200);
        Assert.assertEquals(null, stringOffHeapCache.getValue("ccc"));
    }

    @Test
    public void testObjectOffHeapCache() throws Exception {
        TestEntity entity1 = TestEntity.builder().id(100000L).name("test1").createTime(new Date()).age(20).build();
        log.info("Memory used={}, size={}, maxSize={}", objectOffHeapCache.memUsed(), objectOffHeapCache.getSize(), objectOffHeapCache.getMaxSize());
        objectOffHeapCache.putValue("aaa", entity1);
        TestEntity value = objectOffHeapCache.getValue("aaa");
        log.info("Memory used={}, size={}, maxSize={}", objectOffHeapCache.memUsed(), objectOffHeapCache.getSize(), objectOffHeapCache.getMaxSize());
        Assert.assertEquals("test1", value.getName());
        Assert.assertNotEquals(entity1.hashCode(), value.hashCode());
        log.info(value.getCreateTime().toString());
        log.info(objectOffHeapCache.getValue("aaa").getAge().toString());
        entity1.setName("test2");
        log.info("原缓存数据不会被修改");
        Assert.assertNotEquals("test2", objectOffHeapCache.getValue("aaa").getName());
        objectOffHeapCache.put("aaa", entity1);
        Assert.assertEquals("test2", objectOffHeapCache.getValue("aaa").getName());

        TestEntity entity2 = TestEntity.builder().id(100001L).name("test2").createTime(new Date()).age(21).build();
        objectOffHeapCache.put("bbb", entity2);
        Assert.assertEquals("test2", objectOffHeapCache.getValue("bbb").getName());
        objectOffHeapCache.putValue("ccc", entity2, 100);
        Assert.assertEquals("test2", objectOffHeapCache.getValue("ccc").getName());
        log.info("Memory used={}, size={}, maxSize={}", objectOffHeapCache.memUsed(), objectOffHeapCache.getSize(), objectOffHeapCache.getMaxSize());
        Thread.sleep(200);
        Assert.assertEquals(null, objectOffHeapCache.getValue("ccc"));
        Thread.sleep(110);
        Assert.assertEquals(null, objectOffHeapCache.getValue("aaa"));
        log.info("Memory used={}, size={}, maxSize={}", objectOffHeapCache.memUsed(), objectOffHeapCache.getSize(), objectOffHeapCache.getMaxSize());
    }

    @Test
    public void testObjectOffHeapCacheMulti() {
        for (int i = 0; i < 1000; i++) {
            int finalI = i;
            new Thread(() -> {
                try {
                    for (int j = 0; j < 100; j++) {
                        String name = finalI + "_name";
                        TestEntity entity = objectOffHeapCache.getWithLoader(name);
                        // log.info(entity.getName());
                        Assert.assertEquals(name, entity.getName());
                        assert !name.equals(entity.getName() + "aaa");
                        if (!name.equals(entity.getName())) {
                            log.error("数据有误！key={}", name);
                        } else {
                            log.info("数据正确！key={}", name);
                        }
                    }
                    // testListOffHeapCache();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }).start();
        }
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testListOffHeapCache() throws Exception {
        TestEntity entity1 = TestEntity.builder().id(100000L).name("test1").createTime(new Date()).age(20).build();
        TestEntity entity2 = TestEntity.builder().id(100001L).name("test2").createTime(new Date()).age(21).build();
        TestEntity entity3 = TestEntity.builder().id(100002L).name("test3").createTime(new Date()).age(22).build();
        ArrayList<TestEntity> list = Lists.newArrayList(entity1, entity2, entity3);
        log.info("Memory used={}, size={}, maxSize={}", objectOffHeapCache.memUsed(), objectOffHeapCache.getSize(), objectOffHeapCache.getMaxSize());
        listOffHeapCache.putValue("list1", list);
        for (int i = 0; i < 100000; i++) {
            log.info("This={}", i);
            listOffHeapCache.getWithLoader("list1" + i);
        }
        List<TestEntity> value = listOffHeapCache.getWithLoader("list1");
        log.info("Memory used={}, size={}, maxSize={}", objectOffHeapCache.memUsed(), objectOffHeapCache.getSize(), objectOffHeapCache.getMaxSize());
        Assert.assertEquals(3, value.size());
        Assert.assertNotNull(value.get(2).getName());
        log.info(value.get(2).getName());
        Assert.assertNotNull(value.get(2).getAge());
        log.info(value.get(2).getAge().toString());
        Assert.assertNotNull(value.get(2).getId());
        log.info(value.get(2).getId().toString());
        Assert.assertNotEquals(entity1.hashCode(), value.get(0).hashCode());

        List value2 = listOffHeapCache.getWithLoader("listUnknown");
        listOffHeapCache.getWithLoader("listUnknown");
        // Assert.assertEquals(0, value2.size());
    }
}