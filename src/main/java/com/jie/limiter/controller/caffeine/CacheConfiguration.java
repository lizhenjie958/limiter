package com.jie.limiter.controller.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * maximumSize=100000：最大容量10万条目
 * expireAfterWrite=1h：写入后1小时过期
 * recordStats：开启统计功能（命中率、加载时间）
 *
 * @author ZhuPo
 * @date 2025/12/22 17:09
 */
@Configuration
public class CacheConfiguration {

    @Value("${cache.caffeine.spec:maximumSize=100000,expireAfterWrite=10s,recordStats}")
    private String cacheSpec;

    @Bean("localUrlCache")
    public Cache<String, String> localUrlCache() {
        return Caffeine.from(cacheSpec).build();
    }
}
