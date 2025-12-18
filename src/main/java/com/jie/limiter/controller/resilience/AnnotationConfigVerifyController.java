package com.jie.limiter.controller.resilience;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * 通过注解的方式默认执行的顺序如下，bulkhead先执行，最后是retry
 * Retry ( CircuitBreaker ( RateLimiter ( TimeLimiter ( Bulkhead ( CalledFunction ) ) ) ) )
 * 可以通过配置修改执行的顺序
 *
 * @author ZhuPo
 * @date 2025/12/18 16:25
 */
@RestController
@RequestMapping("/annotation")
@Slf4j
public class AnnotationConfigVerifyController {
    @CircuitBreaker(name = "backendB", fallbackMethod = "fallback")
//    @RateLimiter(name = "backendB")
//    @Bulkhead(name = "backendB")
//    @Retry(name = "backendB", fallbackMethod = "fallback")
//    @TimeLimiter(name = "backendB")
    @GetMapping(value = "/test")
    public String test() throws InterruptedException {
        TimeUnit.SECONDS.sleep(2);
        return "hello";
    }

    private String fallback(IllegalArgumentException e) {
        log.error("error:{}", e.getMessage());
        return "hello fallback";
    }
}
