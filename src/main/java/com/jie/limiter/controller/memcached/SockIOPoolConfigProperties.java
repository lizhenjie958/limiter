package com.jie.limiter.controller.memcached;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author ZhuPo
 * @date 2025/12/23 13:53
 */
@ConfigurationProperties(prefix = "memcache")
@Data
public class SockIOPoolConfigProperties {
    private String[] servers;

    private Integer[] weights;

    private int initConn;

    private int minConn;

    private int maxConn;

    private long maintSleep;

    private boolean nagle;

    private int socketTO;
}
