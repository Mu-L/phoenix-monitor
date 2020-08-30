package com.imby.server.business.server.aop;

import com.alibaba.fastjson.JSON;
import com.imby.common.domain.Server;
import com.imby.common.domain.server.CpuDomain;
import com.imby.common.domain.server.DiskDomain;
import com.imby.common.domain.server.MemoryDomain;
import com.imby.common.dto.ServerPackage;
import com.imby.common.exception.NetException;
import com.imby.common.util.CpuUtils;
import com.imby.server.business.server.core.CpuPool;
import com.imby.server.business.server.core.DiskPool;
import com.imby.server.business.server.core.MemoryPool;
import com.imby.server.business.server.domain.Cpu;
import com.imby.server.business.server.domain.Disk;
import com.imby.server.business.server.domain.Memory;
import com.imby.server.inf.IServerMonitoringListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.hyperic.sigar.SigarException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 处理服务器信息切面。
 * </p>
 * 1.把所有服务器信息添加或更新到Spring容器中的服务器信息池；<br>
 * 2.在所有服务器信息添加或更新到Spring容器中的服务器信息池之后，调用服务器信息监听器回调接口。<br>
 *
 * @author 皮锋
 * @custom.date 2020年4月1日 下午3:21:19
 */
@Slf4j
@Aspect
@Component
public class ServerAspect {

    /**
     * 服务器内存信息池
     */
    @Autowired
    private MemoryPool memoryPool;

    /**
     * 服务器CPU信息池
     */
    @Autowired
    private CpuPool cpuPool;

    /**
     * 服务器磁盘信息池
     */
    @Autowired
    private DiskPool diskPool;

    /**
     * 服务器信息监听器
     */
    @Autowired
    private List<IServerMonitoringListener> serverMonitoringListeners;

    /**
     * 创建一个线程池，用来调用监听器回调接口，这样不阻塞主线程。
     * corePoolSize：核心线程数。核心线程会一直存在，即使没有任务执行；当线程数小于核心线程数的时候，即使有空闲线程，也会一直创建线程直到达到核心线程数；通常设置为1就可以了。<br>
     * maxPoolSize：最大线程数。是线程池里允许存在的最大线程数量。<br>
     * keepAliveTime：线程空闲时间。当线程空闲时间达到keepAliveTime时，线程会退出（关闭），直到线程数等于核心线程数。<br>
     * workQueue：阻塞队列。建议使用有界队列，比如ArrayBlockingQueue。<br>
     * ThreadFactory：线程创建工厂。一般用来设置线程名称的。<br>
     * handler：拒绝策略。一般用来做日志记录等。<br>
     */
    private final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1,
            // 线程数 = Ncpu /（1 - 阻塞系数），CPU密集型阻塞系数相对较小
            (int) (CpuUtils.getAvailableProcessors() / (1 - 0.2)),
            1L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1024),
            new BasicThreadFactory.Builder()
                    // 设置线程名
                    .namingPattern("monitoring-server-listeners-pool-thread-%d")
                    // 设置为守护线程
                    .daemon(true)
                    .build(),
            new ThreadPoolExecutor.AbortPolicy());

    /**
     * <p>
     * 定义切入点，切入点为com.imby.server.business.server.controller.ServerController.acceptServerPackage这一个方法
     * </p>
     *
     * @author 皮锋
     * @custom.date 2020/2/22 17:56
     */
    @Pointcut("execution(public * com.imby.server.business.server.controller.ServerController.acceptServerPackage(..))")
    public void tangentPoint() {
    }

    /**
     * <p>
     * 通过后置通知，在服务器信息包处理完成之后通过切面，刷新Spring容器中的服务器信息池，调用服务器信息监听器回调接口
     * </p>
     *
     * @param joinPoint 提供对连接点上可用状态和有关状态的静态信息的反射访问
     * @author 皮锋
     * @custom.date 2020年4月1日 下午3:34:06
     */
    @AfterReturning("tangentPoint()")
    public void refreshAndWakeUp(JoinPoint joinPoint) {
        String args = String.valueOf(joinPoint.getArgs()[0]);
        ServerPackage serverPackage = JSON.parseObject(args, ServerPackage.class);
        // IP地址
        String ip = serverPackage.getIp();
        // 计算机名
        String computerName = serverPackage.getComputerName();
        // 服务器信息
        Server server = serverPackage.getServer();
        // 内存信息
        MemoryDomain memoryDomain = server.getMemoryDomain();
        // Cpu信息
        CpuDomain cpuDomain = server.getCpuDomain();
        // 磁盘信息
        DiskDomain diskDomain = server.getDiskDomain();
        // 刷新服务器信息
        this.refreshServerInfo(ip, computerName, memoryDomain, cpuDomain, diskDomain);
        // 调用监听器回调接口
        // this.cachedThreadPool.execute(() -> this.serverMonitoringListeners.forEach(e -> e.wakeUp(ip)));
        for (IServerMonitoringListener serverMonitoringListener : this.serverMonitoringListeners) {
            this.threadPoolExecutor.execute(() -> {
                try {
                    serverMonitoringListener.wakeUp(ip);
                } catch (NetException e) {
                    log.error("获取网络信息异常！", e);
                } catch (SigarException e) {
                    log.error("Sigar异常！", e);
                }
            });
        }
    }

    /**
     * <p>
     * 刷新服务器信息
     * </p>
     *
     * @param ip           IP地址
     * @param computerName 计算机名
     * @param memoryDomain 内存信息
     * @param cpuDomain    CPU信息
     * @param diskDomain   磁盘信息
     * @author 皮锋
     * @custom.date 2020/3/31 13:39
     */
    private void refreshServerInfo(String ip, String computerName, MemoryDomain memoryDomain, CpuDomain cpuDomain, DiskDomain diskDomain) {
        // 刷新服务器内存信息
        this.refreshMemory(ip, computerName, memoryDomain);
        // 刷新服务器CPU信息
        this.refreshCpu(ip, computerName, cpuDomain);
        // 刷新服务器磁盘信息
        this.refreshDisk(ip, computerName, diskDomain);
    }

    /**
     * <p>
     * 刷新服务器磁盘信息
     * </p>
     *
     * @param ip           IP地址
     * @param computerName 计算机名
     * @param diskDomain   磁盘信息
     * @author 皮锋
     * @custom.date 2020/3/31 13:44
     */
    private void refreshDisk(String ip, String computerName, DiskDomain diskDomain) {
        Disk disk = new Disk();
        Map<String, Disk.Subregion> subregionMap = new HashMap<>(16);
        for (DiskDomain.DiskInfoDomain diskInfoDomain : diskDomain.getDiskInfoList()) {
            Disk.Subregion subregion = new Disk.Subregion();
            // 盘符名字
            String devName = diskInfoDomain.getDevName();
            // 盘符使用率
            double usePercent = Disk.calculateUsePercent(diskInfoDomain.getUsePercent());
            subregion.setUsePercent(usePercent);
            subregion.setDevName(devName);
            Disk.Subregion poolDiskSubregion = this.diskPool.get(ip) != null ? this.diskPool.get(ip).getSubregionMap().get(devName) : null;
            subregion.setAlarm(poolDiskSubregion != null && poolDiskSubregion.isAlarm());
            subregion.setOverLoad(poolDiskSubregion != null && poolDiskSubregion.isOverLoad());
            subregionMap.put(devName, subregion);
        }
        disk.setIp(ip);
        disk.setComputerName(computerName);
        disk.setSubregionMap(subregionMap);
        this.diskPool.updateDiskPool(ip, disk);
    }

    /**
     * <p>
     * 刷新服务器CPU信息
     * </p>
     *
     * @param ip           IP地址
     * @param computerName 计算机名
     * @param cpuDomain    CPU信息
     * @author 皮锋
     * @custom.date 2020/3/31 13:43
     */
    private void refreshCpu(String ip, String computerName, CpuDomain cpuDomain) {
        Cpu cpu = new Cpu();
        cpu.setIp(ip);
        cpu.setComputerName(computerName);
        cpu.setCpuDomain(cpuDomain);
        cpu.setAvgCpuCombined(Cpu.calculateAvgCpuCombined(cpuDomain));
        Cpu poolCpu = this.cpuPool.get(ip);
        cpu.setNum90(poolCpu != null ? poolCpu.getNum90() : 0);
        cpu.setAlarm90(poolCpu != null && poolCpu.isAlarm90());
        cpu.setOverLoad90(poolCpu != null && poolCpu.isOverLoad90());
        cpu.setNum100(poolCpu != null ? poolCpu.getNum100() : 0);
        cpu.setAlarm100(poolCpu != null && poolCpu.isAlarm100());
        cpu.setOverLoad100(poolCpu != null && poolCpu.isOverLoad100());
        // 更新服务器CPU信息池
        this.cpuPool.updateCpuPool(ip, cpu);
    }

    /**
     * <p>
     * 刷新服务器内存信息
     * </p>
     *
     * @param ip           IP地址
     * @param computerName 计算机名
     * @param memoryDomain 内存信息
     * @author 皮锋
     * @custom.date 2020/3/31 13:41
     */
    private void refreshMemory(String ip, String computerName, MemoryDomain memoryDomain) {
        Memory memory = new Memory();
        memory.setIp(ip);
        memory.setComputerName(computerName);
        memory.setMemoryDomain(memoryDomain);
        memory.setUsedPercent(Memory.calculateUsePercent(memoryDomain.getMenUsedPercent()));
        Memory poolMemory = this.memoryPool.get(ip);
        memory.setNum(poolMemory != null ? poolMemory.getNum() : 0);
        memory.setAlarm(poolMemory != null && poolMemory.isAlarm());
        memory.setOverLoad(poolMemory != null && poolMemory.isOverLoad());
        // 更新服务器内存信息池
        this.memoryPool.updateMemoryPool(ip, memory);
    }

}
