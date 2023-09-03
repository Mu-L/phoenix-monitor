package com.gitee.pifeng.monitoring.common.constant;

import com.gitee.pifeng.monitoring.common.exception.MonitoringUniversalException;
import org.apache.commons.lang3.StringUtils;

/**
 * <p>
 * 通信协议类型
 * </p>
 *
 * @author 皮锋
 * @custom.date 2020年3月9日 下午6:19:20
 */
public enum CommProtocolTypeEnums {

    /**
     * HTTP协议
     */
    HTTP,

    /**
     * WS协议
     */
    WS,

    /**
     * WSS协议
     */
    WSS;

    /**
     * <p>
     * 协议类型字符串转协议类型枚举
     * </p>
     *
     * @param protocolTypeStr 协议类型字符串
     * @return 协议类型枚举
     * @author 皮锋
     * @custom.date 2021/1/28 21:03
     */
    public static CommProtocolTypeEnums str2Enum(String protocolTypeStr) {
        // HTTP协议
        if (StringUtils.equalsIgnoreCase(CommProtocolTypeEnums.HTTP.name(), protocolTypeStr)) {
            return CommProtocolTypeEnums.HTTP;
        }
        // WS 协议
        else if (StringUtils.equalsIgnoreCase(CommProtocolTypeEnums.WS.name(), protocolTypeStr)) {
            return CommProtocolTypeEnums.WS;
        }
        // WSS 协议
        else if (StringUtils.equalsIgnoreCase(CommProtocolTypeEnums.WSS.name(), protocolTypeStr)) {
            return CommProtocolTypeEnums.WSS;
        }
        throw new MonitoringUniversalException("未知的通信协议！");
    }

}
