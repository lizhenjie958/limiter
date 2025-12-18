package com.jie.limiter.controller.resilience;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.Duration;
import java.util.Random;

/**
 * @author ZhuPo
 * @date 2025/12/18 15:40
 */
@RestController
@RequestMapping("/retry")
@Slf4j
public class RetryController {
    private static final Retry retry;


    @RequestMapping("/test")
    public String test() {
        String s = retry.executeSupplier(() -> {
            Random random = new Random();
            if (random.nextInt(10) > 3) {
                log.info("---------------执行失败----------");
                throw new RuntimeException("error");
            }
            log.info("---------------执行成功----------");
            return "success";
        });
        return s;
    }


    static {
        RetryConfig config = RetryConfig.custom()
                // 最大尝试次数
                .maxAttempts(3)
                // 尝试间隔
                .waitDuration(Duration.ofSeconds(1))
                // 修改重试间隔的函数。默认情况下，等待时间保持不变
//                .intervalFunction(Integer::longValue)
                .retryOnException(throwable -> throwable instanceof RuntimeException)
//                .ignoreExceptions(NullPointerException.class)
                .build();

        RetryRegistry retryRegistry = RetryRegistry.of(config);
        retry = retryRegistry.retry("testRetry", config);
    }

}
