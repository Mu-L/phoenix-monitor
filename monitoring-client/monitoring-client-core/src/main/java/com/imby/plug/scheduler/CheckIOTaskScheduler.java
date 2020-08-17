package com.imby.plug.scheduler;

import com.imby.common.domain.Result;
import com.imby.plug.core.ConfigLoader;
import com.imby.plug.thread.CheckIOThread;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 检测HTTP连接IO情况的任务调度器
 * </p>
 *
 * @author 皮锋
 * @custom.date 2020/8/16 11:54
 */
@Slf4j
@Deprecated
public class CheckIOTaskScheduler {

    /**
     * 线程池
     */
    private static final ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(5,
            16,
            15L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1024),
            new BasicThreadFactory.Builder()
                    // 设置线程名
                    .namingPattern("monitoring-checkIO-pool-thread-%d")
                    // 设置为守护线程
                    .daemon(true)
                    .build(),
            new ThreadPoolExecutor.AbortPolicy());

    /**
     * <p>
     * 检测与监控服务端或者监控代理端的IO情况
     * </p>
     *
     * @return 与监控服务端或者监控代理端的IO是否正常
     * @author 皮锋
     * @custom.date 2020/8/16 17:49
     */
    public static boolean call() {
        try {
            Future<Result> scheduledFuture = THREAD_POOL_EXECUTOR.submit(new CheckIOThread());
            Result result = scheduledFuture.get();
            boolean b = result.isSuccess();
            if (!b) {
                // 休眠一会，时间为监控配置文件中配置的心跳频率
                Thread.sleep(ConfigLoader.monitoringProperties.getHeartbeatProperties().getRate() * 1000);
                // 递归调用
                b = call();
            } else {
                // 关闭线程池
                THREAD_POOL_EXECUTOR.shutdown();
            }
            return b;
        } catch (Exception e) {
            log.error("检测与监控服务端或者监控代理端的IO情况异常！", e);
        }
        return false;
    }

}
