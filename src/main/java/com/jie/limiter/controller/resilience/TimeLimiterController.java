package com.jie.limiter.controller.resilience;

import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.Duration;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 超时控制
 * timelimiter与其他的组件有一个区别，就是它的调用必须放在与timelimiter不同的线程中执行，否则不生效
 * @author ZhuPo
 * @date 2025/12/18 16:00
 */
@RestController
@RequestMapping("/timeLimit")
public class TimeLimiterController {

    private static TimeLimiter timeLimiter = null;

    // 被调用服务的运行的线程池
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);

    @RequestMapping("/test")
    public String test() throws Exception {

        // 返回CompletableFuture类型的非阻塞变量
//        CompletionStage<String> error = timeLimiter.executeCompletionStage(scheduler, () -> {
//            return CompletableFuture.supplyAsync(() -> {
//                Random random = new Random();
//                int i = random.nextInt(5);
//                if (i > 1) {
//                    return "success";
//                }
//                throw new RuntimeException("error");
//            });
//        }).toCompletableFuture();

        // 阻塞方式，实际上是调用了future.get(timeoutDuration, MILLISECONDS)
        String s = timeLimiter.executeFutureSupplier(
                () -> CompletableFuture.supplyAsync(() -> {
                    Random random = new Random();
                    int i = random.nextInt(5);
                    try {
                        TimeUnit.SECONDS.sleep(i);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return "success";
                }));
        return s;
    }

    static {
        TimeLimiterConfig config = TimeLimiterConfig.custom()
                // 是否停止正在异步执行的调用
                .cancelRunningFuture(true)
                // 超时时间
                .timeoutDuration(Duration.ofSeconds(3))
                .build();

        TimeLimiterRegistry registry = TimeLimiterRegistry.of(config);
        timeLimiter = registry.timeLimiter("testTimeLimiter");
    }
}
