package com.jie.limiter.controller.resilience;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

/**
 * ratelimiter有两种实现，一种基于SemaphoreBasedRateLimiter（令牌桶），另一种基于AtomicRateLimiter
 * @author ZhuPo
 * @date 2025/12/18 14:47
 */
@RestController
@RequestMapping("/rateLimit")
public class RateLimiterController {

    private static RateLimiter rateLimiter = null;

    @RequestMapping("/test")
    public String test() {
        String s = rateLimiter.executeSupplier(() -> "success");
        return s;
    }


    static {
        RateLimiterConfig config = RateLimiterConfig.custom()
                // 刷新周期
                .limitRefreshPeriod(Duration.ofSeconds(10))
                // 在一次刷新周期内，允许执行的最大请求数
                .limitForPeriod(3)
                // 线程等待调用许可（如可用令牌）的等待时间
                .timeoutDuration(Duration.ofSeconds(1))
                // 当执行结果满足 result.isRight() 条件时，才会消耗令牌
//                .drainPermissionsOnResult(result -> result.isRight())
                // 允许在异常发生时捕获完整的堆栈跟踪信息
//                .writableStackTraceEnabled( true)
                .build();
        RateLimiterRegistry rateLimiterRegistry = RateLimiterRegistry.of(config);
        rateLimiter = rateLimiterRegistry.rateLimiter("testRateLimiter");
    }
}
