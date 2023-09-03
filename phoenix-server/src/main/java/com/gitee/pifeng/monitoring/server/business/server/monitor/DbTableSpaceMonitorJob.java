package com.gitee.pifeng.monitoring.server.business.server.monitor;

import cn.hutool.core.io.unit.DataSizeUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.db.Entity;
import cn.hutool.db.handler.EntityListHandler;
import cn.hutool.db.sql.SqlExecutor;
import com.baomidou.mybatisplus.annotation.DbType;
import com.gitee.pifeng.monitoring.common.constant.MonitorTypeEnums;
import com.gitee.pifeng.monitoring.common.constant.ZeroOrOneConstants;
import com.gitee.pifeng.monitoring.common.constant.alarm.AlarmLevelEnums;
import com.gitee.pifeng.monitoring.common.constant.alarm.AlarmReasonEnums;
import com.gitee.pifeng.monitoring.common.constant.sql.Oracle;
import com.gitee.pifeng.monitoring.common.domain.Alarm;
import com.gitee.pifeng.monitoring.common.dto.AlarmPackage;
import com.gitee.pifeng.monitoring.common.exception.NetException;
import com.gitee.pifeng.monitoring.common.threadpool.ThreadPoolShutdownHelper;
import com.gitee.pifeng.monitoring.common.util.CollectionUtils;
import com.gitee.pifeng.monitoring.common.util.DateTimeUtils;
import com.gitee.pifeng.monitoring.common.util.Md5Utils;
import com.gitee.pifeng.monitoring.common.util.server.ProcessorsUtils;
import com.gitee.pifeng.monitoring.server.business.server.core.MonitoringConfigPropertiesLoader;
import com.gitee.pifeng.monitoring.server.business.server.core.ServerPackageConstructor;
import com.gitee.pifeng.monitoring.server.business.server.domain.DbTableSpace;
import com.gitee.pifeng.monitoring.server.business.server.entity.MonitorDb;
import com.gitee.pifeng.monitoring.server.business.server.service.IAlarmService;
import com.gitee.pifeng.monitoring.server.business.server.service.IDbService;
import com.gitee.pifeng.monitoring.server.util.db.DbUtils;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * <p>
 * 在项目启动后，定时扫描数据库“MONITOR_DB”表中所有数据库连接对应的数据库表空间信息，发送告警。
 * </p>
 *
 * @author 皮锋
 * @custom.date 2021/1/8 10:43
 */
@Slf4j
@Component
@Order(5)
public class DbTableSpaceMonitorJob extends QuartzJobBean {

    /**
     * 监控配置属性加载器
     */
    @Autowired
    private MonitoringConfigPropertiesLoader monitoringConfigPropertiesLoader;

    /**
     * 服务端包构造器
     */
    @Autowired
    private ServerPackageConstructor serverPackageConstructor;

    /**
     * 告警服务接口
     */
    @Autowired
    private IAlarmService alarmService;

    /**
     * 数据库表服务接口
     */
    @Autowired
    private IDbService dbService;

    /**
     * 线程池
     */
    private final ScheduledExecutorService seService = new ScheduledThreadPoolExecutor(
            // 线程数 = Ncpu /（1 - 阻塞系数），IO密集型阻塞系数相对较大
            (int) (ProcessorsUtils.getAvailableProcessors() / (1 - 0.8)),
            new BasicThreadFactory.Builder()
                    // 设置线程名
                    .namingPattern("phoenix-server-db-tbs-monitor-pool-thread-%d")
                    // 设置为守护线程
                    .daemon(true)
                    .build(),
            new ThreadPoolExecutor.AbortPolicy());

    /**
     * 在程序关闭前优雅关闭线程池
     *
     * @author 皮锋
     * @custom.date 2023/6/4 22:00
     */
    @PreDestroy
    public void shutdownThreadPoolGracefully() {
        new ThreadPoolShutdownHelper().shutdownGracefully(this.seService, "phoenix-server-db-tbs-monitor-pool-thread");
    }

    /**
     * <p>
     * 扫描数据库“MONITOR_DB”表中所有数据库连接对应的数据库表空间信息，发送告警。
     * </p>
     *
     * @param jobExecutionContext 作业执行上下文
     * @author 皮锋
     * @custom.date 2021/1/8 10:49
     */
    @Override
    protected void executeInternal(@NonNull JobExecutionContext jobExecutionContext) {
        // 是否监控数据库
        boolean isEnable = this.monitoringConfigPropertiesLoader.getMonitoringProperties().getDbProperties().isEnable();
        if (!isEnable) {
            return;
        }
        synchronized (DbTableSpaceMonitorJob.class) {
            try {
                // 查询数据库中的所有数据库信息
                List<MonitorDb> monitorDbs = this.dbService.list();
                // 打乱
                Collections.shuffle(monitorDbs);
                // 按每个list大小为10拆分成多个list
                List<List<MonitorDb>> subMonitorDbLists = CollectionUtils.split(monitorDbs, 10);
                for (List<MonitorDb> subMonitorDbs : subMonitorDbLists) {
                    // 使用多线程，加快处理速度
                    this.seService.execute(() -> {
                        for (MonitorDb monitorDb : subMonitorDbs) {
                            try {
                                // 数据库是否在线（可连接）
                                boolean isOnline = ZeroOrOneConstants.ONE.equals(monitorDb.getIsOnline());
                                // 数据库在线（可连接）
                                if (isOnline) {
                                    // 计算表空间，如果表空间不足，则发送告警
                                    this.calculateTableSpace(monitorDb);
                                }
                            } catch (Exception e) {
                                log.error("执行数据库表空间监控异常！", e);
                            }
                        }
                    });
                }
            } catch (Exception e) {
                log.error("定时扫描数据库“MONITOR_DB”表中所有数据库连接对应的数据库表空间信息异常！", e);
            }
        }
    }

    /**
     * <p>
     * 计算表空间，如果表空间不足，则发送告警
     * </p>
     *
     * @param monitorDb 数据库表
     * @throws SQLException SQL异常
     * @throws NetException 获取数据库信息异常
     * @author 皮锋
     * @custom.date 2021/1/8 13:01
     */
    private void calculateTableSpace(MonitorDb monitorDb) throws SQLException, NetException {
        // 数据库类型
        String dbType = monitorDb.getDbType();
        // oracle
        if (StringUtils.equalsIgnoreCase(dbType, DbType.ORACLE.getDb())) {
            // url
            String url = monitorDb.getUrl();
            //用户名
            String username = monitorDb.getUsername();
            // 密码
            String password = monitorDb.getPassword();
            // 数据库连接
            @Cleanup
            Connection connection = DbUtils.getConnection(url, username, password);
            List<Entity> entityList = SqlExecutor.query(connection, Oracle.TABLE_SPACE_SELECT_ALL, new EntityListHandler());
            for (Entity entity : entityList) {
                // 过载阈值
                double overloadThreshold = this.monitoringConfigPropertiesLoader.getMonitoringProperties().getDbProperties().getDbTableSpaceProperties().getOverloadThreshold();
                double usedRate = NumberUtil.round(entity.getDouble("USEDRATE"), 4).doubleValue();
                String tablespaceName = entity.getStr("TABLESPACENAME", StandardCharsets.UTF_8);
                String total = DataSizeUtil.format(entity.getLong("TOTAL"));
                String used = DataSizeUtil.format(entity.getLong("USED"));
                String free = DataSizeUtil.format(entity.getLong("FREE"));
                double freeRate = NumberUtil.round(entity.getDouble("FREERATE"), 4).doubleValue();
                DbTableSpace dbTableSpace = DbTableSpace.builder()
                        .tablespaceName(tablespaceName)
                        .total(total)
                        .used(used)
                        .free(free)
                        .freeRate(freeRate)
                        .usedRate(usedRate)
                        .build();
                // 表空间不够
                if (usedRate >= overloadThreshold) {
                    // 告警级别
                    AlarmLevelEnums alarmLevelEnum = this.monitoringConfigPropertiesLoader.getMonitoringProperties().getDbProperties().getDbTableSpaceProperties().getLevelEnum();
                    // 发送告警
                    this.sendAlarmInfo("数据库表空间不够", monitorDb, dbTableSpace, alarmLevelEnum, AlarmReasonEnums.NORMAL_2_ABNORMAL);
                }
                // 表空间正常
                else {
                    // 不用担心头次检测到数据库表空间正常（非异常转正常）会发送告警，最终是否发送告警由“实时监控服务”决定
                    this.sendAlarmInfo("数据库表空间恢复", monitorDb, dbTableSpace, AlarmLevelEnums.INFO, AlarmReasonEnums.ABNORMAL_2_NORMAL);
                }
            }
        }
    }

    /**
     * <p>
     * 发送告警信息
     * </p>
     *
     * @param title           告警标题
     * @param monitorDb       数据库信息
     * @param dbTableSpace    数据库表空间
     * @param alarmLevelEnum  告警级别
     * @param alarmReasonEnum 告警原因
     * @throws NetException 获取数据库信息异常
     * @author 皮锋
     * @custom.date 2020/12/20 21:25
     */
    private void sendAlarmInfo(String title, MonitorDb monitorDb, DbTableSpace dbTableSpace, AlarmLevelEnums
            alarmLevelEnum, AlarmReasonEnums alarmReasonEnum)
            throws NetException {
        StringBuilder builder = new StringBuilder();
        builder.append("连接名：").append(monitorDb.getConnName())
                .append("，<br>url：").append(monitorDb.getUrl())
                .append("，<br>类型：").append(monitorDb.getDbType());
        if (StringUtils.isNotBlank(monitorDb.getDbDesc())) {
            builder.append("，<br>描述：").append(monitorDb.getDbDesc());
        }
        builder.append("，<br>表空间名：").append(dbTableSpace.getTablespaceName());
        builder.append("，<br>总空间：").append(dbTableSpace.getTotal());
        builder.append("，<br>使用空间：").append(dbTableSpace.getUsed());
        builder.append("，<br>剩余空间：").append(dbTableSpace.getFree());
        builder.append("，<br>使用率：").append(dbTableSpace.getUsedRate()).append("%");
        builder.append("，<br>剩余率：").append(dbTableSpace.getFreeRate()).append("%");
        builder.append("，<br>时间：").append(DateTimeUtils.dateToString(new Date()));
        Alarm alarm = Alarm.builder()
                // 保证code的唯一性
                .code(Md5Utils.encrypt32(monitorDb.getUrl() + monitorDb.getUsername() + dbTableSpace.getTablespaceName()))
                .title(title)
                .msg(builder.toString())
                .alarmLevel(alarmLevelEnum)
                .alarmReason(alarmReasonEnum)
                .monitorType(MonitorTypeEnums.DATABASE)
                .build();
        AlarmPackage alarmPackage = this.serverPackageConstructor.structureAlarmPackage(alarm);
        this.alarmService.dealAlarmPackage(alarmPackage);
    }
}