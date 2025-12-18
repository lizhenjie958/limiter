package com.jie.limiter.controller.resilience;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.Duration;
import java.util.Random;

/**
 * @author ZhuPo
 * @date 2025/12/18 11:21
 */
@RestController
@RequestMapping("/circuit")
@Slf4j
public class CircuitBreakerController {
    private static CircuitBreaker circuitBreaker = null;

    @GetMapping("/test")
    public String test(@RequestParam("num") Integer num) {
        // CircuitBreakerRegistry defaultRegistry = CircuitBreakerRegistry.ofDefaults();
        String result = circuitBreaker.executeSupplier(() -> {
            Random random = new Random();
            if(num > 1){
                return "success";
            }
            if (random.nextInt(10) > 2) {
                throw new RuntimeException("error");
            }
            return "success";
        });

        // 监听事件
//        circuitBreaker.getEventPublisher()
//                .onSuccess(event -> log.info("onSuccess"))
//                .onError(event -> log.info("onError"))
//                .onIgnoredError(event -> log.info("onIgnoredError"))
//                .onReset(event -> log.info("onReset"))
//                .onStateTransition(event -> log.info("onStateTransition"));

        // 统一处理所有的事件
        circuitBreaker.getEventPublisher()
                .onEvent(event -> log.info("event:{}",event));

        return result;
    }



    static {
        // 配置
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                // 记录为失败并因此增加失败率的异常列表
                .recordExceptions(RuntimeException.class)
                // 忽略的异常列表
                .ignoreExceptions(NullPointerException.class)
                // 记录为成功并因此减少失败率的异常列表
//                .recordResult(result -> result.equals("success"))
                // 以百分比配置失败率阈值。当失败率等于或大于阈值时，断路器状态开启，并进行服务降级
                .failureRateThreshold(10)

                // 配置调用时间的阈值，高于该阈值的请求视为慢调用
                .slowCallDurationThreshold(Duration.ofSeconds(1))
                // 以百分比的方式配置，断路器把调用时间大于slowCallDurationThreshold的调用视为慢调用，当慢调用比例大于等于阈值时，断路器开启，并进行服务降级。
                .slowCallRateThreshold(50)

                // 断路器计算失败率或慢调用率之前所需的最小调用数（每个滑动窗口周期）。作用与关闭状态下的最小调用数。
                // 例如，如果minimumNumberOfCalls为10，则必须至少记录10个调用，然后才能计算失败率。如果只记录了9次调用，即使所有9次调用都失败，断路器也不会开启。
                .minimumNumberOfCalls(5)

                // 断路器从开启过渡到半开应等待的时间
                .waitDurationInOpenState(Duration.ofSeconds(10))
                // 如果设置为true，则意味着断路器将自动从开启状态过渡到半开状态，并且不需要调用来触发转换
                .automaticTransitionFromOpenToHalfOpenEnabled(false)
                // 当断路器从OPEN状态过渡到HALF_OPEN状态时，只允许最多20个请求通过进行
                // 目的：逐步恢复服务，避免大量请求瞬间涌入导致服务再次崩
                .permittedNumberOfCallsInHalfOpenState(20)

                // 配置滑动窗口的类型，当断路器关闭时，将调用的结果记录在滑动窗口中
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.TIME_BASED)
                // 配置滑动窗口的大小，单位s
                .slidingWindowSize(10)

                .build();

        // 注册配置
        CircuitBreakerRegistry circuitBreakerRegistry = CircuitBreakerRegistry.of(circuitBreakerConfig);
        circuitBreaker = circuitBreakerRegistry.circuitBreaker("testCircuitBreaker");

        // 事件
        circuitBreakerRegistry.getEventPublisher()
                .onEntryAdded(entryAddedEvent -> {
                    // 注册了新的circuitbreaker
                    CircuitBreaker addedCircuitBreaker = entryAddedEvent.getAddedEntry();
                    log.info("CircuitBreaker {} added", addedCircuitBreaker.getName());
                })
                .onEntryRemoved(entryRemovedEvent -> {
                    // 注销circuitbreaker
                    CircuitBreaker removedCircuitBreaker = entryRemovedEvent.getRemovedEntry();
                    log.info("CircuitBreaker {} removed", removedCircuitBreaker.getName());
                });
    }

}
