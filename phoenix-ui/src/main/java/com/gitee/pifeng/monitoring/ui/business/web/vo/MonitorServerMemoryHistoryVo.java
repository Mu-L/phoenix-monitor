package com.gitee.pifeng.monitoring.ui.business.web.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.gitee.pifeng.monitoring.common.inf.ISuperBean;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorServerMemoryHistory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import java.util.Date;

/**
 * <p>
 * 服务器内存历史记录表现层对象
 * </p>
 *
 * @author 皮锋
 * @custom.date 2021-01-24
 */
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "服务器内存历史记录表现层对象")
public class MonitorServerMemoryHistoryVo implements ISuperBean {

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "IP地址")
    private String ip;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "物理内存总量（单位：byte）")
    private Long memTotal;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "物理内存使用量（单位：byte）")
    private Long memUsed;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "物理内存剩余量（单位：byte）")
    private Long memFree;

    @Schema(description = "物理内存使用率")
    private Double menUsedPercent;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "交换区总量（单位：byte）")
    private Long swapTotal;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "交换区使用量（单位：byte）")
    private Long swapUsed;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "交换区剩余量（单位：byte）")
    private Long swapFree;

    @Schema(description = "交换区使用率")
    private Double swapUsedPercent;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "新增时间")
    private Date insertTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "更新时间")
    private Date updateTime;

    /**
     * <p>
     * MonitorServerMemoryHistoryVo转MonitorServerMemoryHistory
     * </p>
     *
     * @return {@link MonitorServerMemoryHistory}
     * @author 皮锋
     * @custom.date 2020/9/3 9:20
     */
    public MonitorServerMemoryHistory convertTo() {
        MonitorServerMemoryHistory monitorServerMemoryHistory = MonitorServerMemoryHistory.builder().build();
        BeanUtils.copyProperties(this, monitorServerMemoryHistory);
        return monitorServerMemoryHistory;
    }

    /**
     * <p>
     * MonitorServerMemoryHistory转MonitorServerMemoryHistoryVo
     * </p>
     *
     * @param monitorServerMemoryHistory {@link MonitorServerMemoryHistory}
     * @return {@link MonitorServerMemoryHistoryVo}
     * @author 皮锋
     * @custom.date 2020/9/3 9:22
     */
    public MonitorServerMemoryHistoryVo convertFor(MonitorServerMemoryHistory monitorServerMemoryHistory) {
        if (null != monitorServerMemoryHistory) {
            BeanUtils.copyProperties(monitorServerMemoryHistory, this);
        }
        return this;
    }

}
