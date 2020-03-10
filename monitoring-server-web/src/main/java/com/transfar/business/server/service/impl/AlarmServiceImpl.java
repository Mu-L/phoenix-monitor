package com.transfar.business.server.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.transfar.business.server.domain.TransfarSms;
import com.transfar.business.server.service.IAlarmService;
import com.transfar.business.server.service.ISmsService;
import com.transfar.constant.AlarmTypeEnums;
import com.transfar.constant.EnterpriseConstants;
import com.transfar.dto.AlarmPackage;
import com.transfar.property.MonitoringServerWebProperties;

/**
 * <p>
 * 告警服务实现
 * </p>
 *
 * @author 皮锋
 * @custom.date 2020年3月10日 下午1:30:07
 */
@Service
public class AlarmServiceImpl implements IAlarmService {

    /**
     * 短信服务接口
     */
    @Autowired
    private ISmsService smsService;

    /**
     * 监控配置属性
     */
    @Autowired
    private MonitoringServerWebProperties config;

    /**
     * <p>
     * 处理心跳包
     * </p>
     *
     * @param alarmPackage 心跳包
     * @return Boolean
     * @author 皮锋
     * @custom.date 2020年3月10日 下午1:33:55
     */
    @Override
    public Boolean dealAlarmPackage(AlarmPackage alarmPackage) {
        // 返回结果
        boolean result = false;
        // 是测试告警信息，不作处理，直接返回true
        if (alarmPackage.isTest()) {
            // 停止往下执行
            return true;
        }
        // 告警内容
        String msg = alarmPackage.getMsg();
        // 告警级别
        String level = alarmPackage.getLevel();
        // 告警方式
        String alarmType = this.config.getAlarmProperties().getType();
        // 短信告警
        if (StringUtils.equalsIgnoreCase(alarmType, AlarmTypeEnums.SMS.name())) {
            // 处理短信告警
            result = this.dealSmsAlarm(msg, level);
        }
        return result;
    }

    /**
     * <p>
     * 处理短信告警
     * </p>
     *
     * @param msg   告警信息
     * @param level 告警级别
     * @return boolean
     * @author 皮锋
     * @custom.date 2020年3月10日 下午3:13:35
     */
    private boolean dealSmsAlarm(String msg, String level) {
        // 返回结构结果
        boolean result;
        // 短信接口商家
        String enterprise = this.config.getAlarmProperties().getSmsProperties().getEnterprise();
        // 判断短信接口公司，不同的公司调用不同的接口
        if (StringUtils.equalsIgnoreCase(EnterpriseConstants.TRANSFAR, enterprise)) {
        	// 调用创发短信接口
            result = this.callTransfarSmsApi(msg, level);
        } else {
            result = false;
        }
        return result;
    }

    /**
     * <p>
     * TODO
     * </p>
     *
     * @param msg   告警内容
     * @param level 告警级别
     * @return boolean
     * @author 皮锋
     * @custom.date 2020年3月10日 下午3:19:36
     */
    private boolean callTransfarSmsApi(String msg, String level) {
        String[] phones = this.config.getAlarmProperties().getSmsProperties().getPhoneNumbers();
        String enterprise = this.config.getAlarmProperties().getSmsProperties().getEnterprise();
        TransfarSms transfarSms = TransfarSms.builder().content(msg).type(level).phone(TransfarSms.getPhones(phones))
                .identity(enterprise).build();
        // 创发公司短信接口
        String str = this.smsService.sendSmsByTransfarApi(transfarSms);
        // 短信发送成功
        return (!StringUtils.equalsIgnoreCase("null", str)) && StringUtils.isNotBlank(str);
    }

}
