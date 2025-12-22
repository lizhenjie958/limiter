package com.jie.limiter.controller.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;
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
 * Cache支持缓存统计和监控，缓存淘汰算法（W-TinyLFU）
 * @author ZhuPo
 * @date 2025/12/19 11:43
 */
@RestController
@RequestMapping("/localCache")
@Slf4j
public class LocalCacheController {
    // 对象方式注入，对象方式注入的缓存会自动初始化
    private static final Cache<String, String> CACHE_BY_TIME =
            Caffeine.newBuilder()
                    // 设置最后一次写入后经过固定时间过期
                    .expireAfterWrite(10, TimeUnit.SECONDS)
                    .recordStats()
            .maximumSize(10000)
            .build();

    // 方式2：访问后过期（适合会话数据）
    private static final Cache<String, String> CACHE_BY_ACCESS =
            Caffeine.newBuilder()
                    .expireAfterAccess(10, TimeUnit.SECONDS)
                    .recordStats()
            .maximumSize(10000)
            .build();



    @Resource
    private Cache<String, String> localUrlCache;

    @GetMapping("/getLongUrl")
    public String getLongUrl(@RequestParam("shortCode") String shortCode) {
        // 查询缓存
        String cached = localUrlCache.getIfPresent(shortCode);
        if (cached != null) return cached;

        // 未命中，查DB后回填
        String longUrl = queryDB(shortCode);
        localUrlCache.put(shortCode, longUrl);
        return longUrl;
    }

    @GetMapping("/stats")
    public CacheStats stats() {
        // 查看缓存指标
        CacheStats stats = localUrlCache.stats();
        log.info("命中率: {}", stats.hitRate());
        log.info("加载次数: {}", stats.loadCount());
        log.info("驱逐次数: {}", stats.evictionCount());
        log.info("驱逐权重: {}", stats.evictionWeight());
        log.info("平均加载耗时: {}", stats.averageLoadPenalty());
        log.info("总加载耗时: {}", stats.totalLoadTime());
        log.info("总请求数: {}", stats.requestCount());
        log.info("总命中数: {}", stats.hitCount());
        log.info("总未命中数: {}", stats.missCount());
        log.info("总加载失败数: {}", stats.loadFailureCount());
        log.info("总加载成功数: {}", stats.loadSuccessCount());
        log.info("加载失败率: {}", stats.loadFailureRate());
        log.info("加载成功率: {}", stats.loadSuccessCount());
        log.info("总未命中率: {}", stats.missRate());
        return stats;
    }


    public String queryDB(String shortCode) {
        return UUID.randomUUID().toString();
    }


    @PostConstruct
    public void warmupCache() {
        // 启动时预热Top热点数据
        localUrlCache.put("123456", "https://www.baidu.com");
    }

}
