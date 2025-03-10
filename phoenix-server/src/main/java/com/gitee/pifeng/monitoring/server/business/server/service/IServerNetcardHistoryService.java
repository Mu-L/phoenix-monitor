package com.gitee.pifeng.monitoring.server.business.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gitee.pifeng.monitoring.common.dto.ServerPackage;
import com.gitee.pifeng.monitoring.server.business.server.entity.MonitorServerNetcardHistory;

/**
 * <p>
 * 服务器网卡历史记录信息服务层接口
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022/4/5 15:21
 */
public interface IServerNetcardHistoryService extends IService<MonitorServerNetcardHistory> {

    /**
     * <p>
     * 把服务器网卡历史记录添加到数据库
     * </p>
     *
     * @param serverPackage 服务器信息包
     * @author 皮锋
     * @custom.date 2020/5/11 23:39
     */
    void operateServerNetcardHistory(ServerPackage serverPackage);

}
