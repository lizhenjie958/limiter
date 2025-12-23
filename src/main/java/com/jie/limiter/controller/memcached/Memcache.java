package com.jie.limiter.controller.memcached;

import com.whalin.MemCached.MemCachedClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * @author ZhuPo
 * @date 2025/12/23 13:58
 */
@Slf4j
public class Memcache {

    private MemCachedClient memCachedClient;

    public Memcache(MemCachedClient memCachedClient) {
        this.memCachedClient = memCachedClient;
    }

    /**
     * 将netty客户端信息存入memcached
     * @param obj
     * @return
     */
    public boolean addChannel(String key, Object obj) {
        try {
            if (obj == null) {
                return false;
            }
            return memCachedClient.set(key, obj);
        } catch (Exception e) {
            log.error("netty客户端插入memcache出错", e);
            return false;
        }
    }

    /**
     * 将netty客户端信息从memcached中移除
     * @param key
     * @return
     */
    public boolean removeChannel(String key) {
        try {
            if (StringUtils.isEmpty(key)) {
                return false;
            }

            return memCachedClient.delete(key);
        } catch (Exception e) {
            log.error("netty客户端移除memcache出错", e);
            return false;
        }
    }

    /**
     * 从memcache中获取netty客户端
     * @param key
     * @return
     */
    public Object getChannel(String key) {
        try {
            if (StringUtils.isEmpty(key)) {
                return null;
            }
            return memCachedClient.get(key);
        } catch (Exception e) {
            log.error("从memcache中获取客户端出错", e);
            return null;
        }
    }

    /**
     * 检测memcache中设备是否登录过
     * @param key
     * @return
     */
    public boolean exist(String key) {
        try {
            if (StringUtils.isEmpty(key)) {
                return false;
            }

            return memCachedClient.keyExists(key);
        } catch (Exception e) {
            log.error("检测memcache中是否存在某个设备出错", e);
            return false;
        }
    }
}
