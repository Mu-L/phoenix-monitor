package com.imby.server.business.server.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * 服务器操作系统表
 * </p>
 *
 * @author 皮锋
 * @custom.date 2020/5/11 15:33
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("MONITOR_SERVER_OS")
public class MonitorServerOs {

    /**
     * 主键ID
     */
    @TableId("ID")
    private int id;

    /**
     * IP地址
     */
    @TableField("IP")
    private String ip;

    /**
     * 服务器名
     */
    @TableField("SERVER_NAME")
    private String serverName;

    /**
     * 操作系统名称
     */
    @TableField("OS_NAME")
    private String osName;

    /**
     * 操作系统版本
     */
    @TableField("OS_VERSION")
    private String osVersion;

    /**
     * 用户名称
     */
    @TableField("USER_NAME")
    private String userName;

    /**
     * 用户主目录
     */
    @TableField("USER_HOME")
    private String userHome;

    /**
     * 操作系统时区
     */
    @TableField("OS_TIME_ZONE")
    private String osTimeZone;

    /**
     * 新增时间
     */
    @TableField("INSERT_TIME")
    private Date insertTime;

    /**
     * 更新时间
     */
    @TableField("UPDATE_TIME")
    private Date updateTime;

}
