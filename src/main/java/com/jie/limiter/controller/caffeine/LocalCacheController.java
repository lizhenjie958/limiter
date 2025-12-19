package com.jie.limiter.controller.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * 1.自动加载条目到缓存中，可选异步方式
 * 2.可以基于大小剔除
 * 3.可以设置过期时间，时间可以从上次访问或上次写入开始计算
 * 4.异步刷新
 * 5.keys自动包装在弱引用中
 * 6.values自动包装在弱引用或软引用中
 * 7.条目剔除通知
 * 8.缓存访问统计
 *
 * Cache支持缓存统计和监控
 * @author ZhuPo
 * @date 2025/12/19 11:43
 */
@RestController
public class LocalCacheController {
    // 构建caffeine的缓存对象，并指定在写入后的10分钟内有效，且最大允许写入的条目数为10000
    private static final Cache<String, String> cache =
            Caffeine.newBuilder()
                    .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(10000)
            .build();
    public static void main(String[] args) {

        String key = "hello";
        // 查找某个缓存元素，若找不到则返回null
        String str = cache.getIfPresent(key);
        System.out.println("cache.getIfPresent(key) ---> " + str);
        // 查找某个缓存元素，若找不到则调用函数生成，如无法生成则返回null
        str = cache.get(key,k->key);
        System.out.println("cache.get(key, k -> create(key)) ---> " + str);
        // 添加或者更新一个缓存元素
        cache.put(key, "aaaa");
        System.out.println("cache.put(key, str) ---> " + cache.getIfPresent(key));
        // 移除一个缓存元素
        cache.invalidate(key);
        System.out.println("cache.invalidate(key) ---> " + cache.getIfPresent(key));
    }
}
