package com.jie.limiter.controller.guava;

import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * RateLimiter（令牌桶算法）
 * 限流维度：基于速率限制，控制单位时间内的请求量
 * 核心概念：令牌桶，以固定速率产生令牌
 * 使用方式：RateLimiter.create(10) 表示每秒允许10个请求
 * 时间平滑：自动处理请求的分布，支持突发流量
 *
 * Semaphore（信号量）
 * 限流维度：基于并发数限制，控制同时执行的线程数
 * 核心概念：许可证数量，控制资源访问
 * 使用方式：new Semaphore(10) 表示最多允许10个线程同时执行
 * 并发控制：主要用于保护有限资源不被过度占用
 *
 * @author ZhuPo
 * @date 2025/12/19 10:57
 */
@RestController
@RequestMapping("/guavaRateLimit")
@Slf4j
public class GRateLimiterController {

    private static final RateLimiter rateLimiter = RateLimiter.create(10);

    @GetMapping("/test")
    public String test() {
        RateLimiter rateLimiter = RateLimiter.create(10.5);

        boolean passed = rateLimiter.tryAcquire(1, 50, TimeUnit.MILLISECONDS);
        if(passed){
            log.info("passed");
            return "passed";
        }else {
            log.info("blocked");
            return "blocked";
        }
    }
}
