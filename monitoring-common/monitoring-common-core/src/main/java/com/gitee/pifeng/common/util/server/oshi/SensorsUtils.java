package com.gitee.pifeng.common.util.server.oshi;

import com.gitee.pifeng.common.domain.server.SensorsDomain;
import com.google.common.collect.Lists;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.Sensors;

import java.util.List;

/**
 * <p>
 * 传感器工具类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2021/1/14 16:21
 */
public class SensorsUtils {

    /**
     * <p>
     * 私有化构造方法
     * </p>
     *
     * @author 皮锋
     * @custom.date 2021/1/14 17:07
     */
    private SensorsUtils() {
    }

    /**
     * <p>
     * 获取传感器信息
     * </p>
     *
     * @return {@link SensorsDomain}
     * @author 皮锋
     * @custom.date 2021/1/14 17:18
     */
    public static SensorsDomain getSensorsInfo() {
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hardwareAbstractionLayer = systemInfo.getHardware();
        Sensors sensors = hardwareAbstractionLayer.getSensors();
        double cpuTemperature = sensors.getCpuTemperature();
        double cpuVoltage = sensors.getCpuVoltage();
        int[] fanSpeeds = sensors.getFanSpeeds();
        SensorsDomain sensorDomain = new SensorsDomain();
        sensorDomain.setCpuTemperature(cpuTemperature != 0 ? String.valueOf(cpuTemperature) : "未知");
        sensorDomain.setCpuVoltage(cpuVoltage != 0 ? String.valueOf(cpuVoltage) : "未知");
        List<SensorsDomain.FanSpeedDomain> fanSpeedDomains = Lists.newArrayList();
        for (int fanSpeed : fanSpeeds) {
            SensorsDomain.FanSpeedDomain fanSpeedDomain = new SensorsDomain.FanSpeedDomain();
            fanSpeedDomain.setFanSpeed(fanSpeed != 0 ? String.valueOf(fanSpeed) : "未知");
            fanSpeedDomains.add(fanSpeedDomain);
            sensorDomain.setFanSpeedDomainList(fanSpeedDomains);
        }
        return sensorDomain;
    }

}
