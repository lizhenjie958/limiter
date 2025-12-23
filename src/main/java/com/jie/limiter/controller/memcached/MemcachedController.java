package com.jie.limiter.controller.memcached;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @author ZhuPo
 * @date 2025/12/23 14:02
 */
@RestController
@RequestMapping("/memcached")
public class MemcachedController {

    @Resource
    private Memcache memcache;

    @GetMapping("/get")
    public Object get(@RequestParam("key") String key) {
        Object channel = memcache.getChannel(key);
        if (channel != null) {
            return channel;
        }
        String value = UUID.randomUUID().toString();
        memcache.addChannel(key, value);
        return value;
    }
}
