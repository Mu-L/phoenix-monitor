package com.gitee.pifeng.server.business.server.pool;

import com.gitee.pifeng.common.inf.ISuperBean;
import com.gitee.pifeng.server.business.server.domain.Cpu;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * 服务器CPU信息池
 * </p>
 *
 * @author 皮锋
 * @custom.date 2020/3/27 15:49
 */
@SuppressWarnings("serial")
@Component
public class CpuPool extends ConcurrentHashMap<String, Cpu> implements ISuperBean {

    /**
     * <p>
     * 更新服务器CPU信息池
     * </p>
     *
     * @param key 服务器CPU信息键
     * @param cpu 服务器CPU信息
     * @author 皮锋
     * @custom.date 2020/3/26 15:53
     */
    public void updateCpuPool(String key, Cpu cpu) {
        Cpu poolCpu = this.get(key);
        if (poolCpu == null) {
            this.put(key, cpu);
        } else {
            this.replace(key, cpu);
        }
    }
}
