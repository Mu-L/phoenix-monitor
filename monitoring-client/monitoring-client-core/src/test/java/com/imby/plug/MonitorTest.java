package com.imby.plug;

import com.google.common.base.Charsets;
import com.imby.common.constant.AlarmLevelEnums;
import com.imby.common.constant.AlarmTypeEnums;
import com.imby.common.domain.Alarm;
import com.imby.common.domain.Result;
import org.junit.Test;

/**
 * <p>
 * 测试监控客户端入口类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2020/8/17 9:53
 */
public class MonitorTest {

    /**
     * <p>
     * 测试发送告警信息
     * </p>
     *
     * @author 皮锋
     * @custom.date 2020/8/17 9:57
     */
    @Test
    public void testSendAlarm() {
        // 开启监控
        Monitor.start();
        // 封装告警信息
        Alarm alarm = Alarm.builder().alarmLevel(AlarmLevelEnums.INFO)
                .alarmType(AlarmTypeEnums.CUSTOM)
                .title("测试发送告警信息")
                .msg("测试发送告警信息")
                .charset(Charsets.UTF_8)
                .isTest(false)
                .build();
        // 发送告警信息
        Result result = Monitor.sendAlarm(alarm);
        System.out.println(result.toJsonString());
    }
}