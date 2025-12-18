package com.jie.limiter.controller.resilience;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * bulkhead实质上是限制调用的并发度。它有两种实现：SemaphoreBulkhead和FixedThreadPoolBulkhead
 * @author ZhuPo
 * @date 2025/12/18 14:58
 */
@RestController
@RequestMapping("bulkhead")
@Slf4j
public class BulkheadController {

    private static Bulkhead bulkhead = null;

    @RequestMapping("test")
    public String test() {
        String s = bulkhead.executeSupplier(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "success";
        });
        return s;
    }


    static {
        BulkheadConfig config = BulkheadConfig.custom()
                // 允许并发执行的最大数量
                .maxConcurrentCalls(3)
                // 当达到并发调用数量时，新的调用将被阻塞，这个属性表示最长的等待时间。
                .maxWaitDuration(Duration.ofSeconds(1))
                // 启用公平调用处理策略，默认为false
                .fairCallHandlingStrategyEnabled(false)
                .build();

        BulkheadRegistry bulkheadRegistry = BulkheadRegistry.of(config);

        bulkhead = bulkheadRegistry.bulkhead("testBulkhead");
    }
}
