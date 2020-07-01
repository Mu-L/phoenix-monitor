package com.imby.common.domain;

import com.imby.common.abs.AbstractSuperBean;
import com.imby.common.constant.AlarmLevelEnums;
import com.imby.common.constant.AlarmTypeEnums;
import lombok.*;
import lombok.experimental.Accessors;

import java.nio.charset.Charset;

/**
 * <p>
 * 告警
 * </p>
 *
 * @author 皮锋
 * @custom.date 2020年3月7日 下午2:35:27
 */
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class Alarm extends AbstractSuperBean {

    /**
     * 告警级别，默认为：WARN
     *
     * @see AlarmLevelEnums
     */
    @Builder.Default
    private AlarmLevelEnums alarmLevel = AlarmLevelEnums.WARN;

    /**
     * 告警类型
     *
     * @see AlarmTypeEnums
     */
    private AlarmTypeEnums alarmType;

    /**
     * 字符集，如果当前字符集不是UTF-8，请指明字符集
     *
     * @see Charset
     */
    private Charset charset;

    /**
     * 是否是测试告警，测试告警服务端不发送告警消息
     */
    private boolean isTest;

    /**
     * 告警标题。<br>
     * 如果设置了告警编码，会根据告警编码查询数据库中对应的告警标题。<br>
     * 注意：数据库中对应的告警标题 会 覆盖直接设置的告警标题。
     *
     * @see Alarm#code
     */
    private String title;

    /**
     * 告警内容。<br>
     * 如果设置了告警编码，会根据告警编码查询数据库中对应的告警内容。<br>
     * 注意：数据库中对应的告警内容 会 覆盖直接设置的告警内容。
     *
     * @see Alarm#code
     */
    private String msg;

    /**
     * 告警编码。<br>
     * 每一个自定义的业务告警，都可以设置一个唯一的编码，用此编码来查找数据库中对应的告警级别、告警标题、告警内容，如果不设置编码，
     * 将直接使用设置的告警级别、告警标题、告警内容。<br>
     * 注意：编码在数据库中对应的告警级别、告警标题、告警内容 会 覆盖直接设置的告警级别、告警标题、告警内容。
     */
    private String code;

}
