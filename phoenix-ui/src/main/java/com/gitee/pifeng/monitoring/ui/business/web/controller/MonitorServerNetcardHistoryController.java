package com.gitee.pifeng.monitoring.ui.business.web.controller;

import com.gitee.pifeng.monitoring.ui.business.web.service.IMonitorServerNetcardHistoryService;
import com.gitee.pifeng.monitoring.ui.business.web.vo.LayUiAdminResultVo;
import com.gitee.pifeng.monitoring.ui.business.web.vo.ServerDetailPageServerNetworkSpeedChartVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * <p>
 * 服务器网卡历史记录
 * </p>
 *
 * @author 皮锋
 * @custom.date 2021-01-25
 */
@Tag(name = "服务器.服务器网卡历史记录")
@Controller
@RequestMapping("/monitor-server-netcard-history")
public class MonitorServerNetcardHistoryController {

    /**
     * 服务器网卡历史记录服务类
     */
    @Autowired
    private IMonitorServerNetcardHistoryService monitorServerNetcardHistoryService;

    /**
     * <p>
     * 获取服务器详情页面服务器网速图表信息
     * </p>
     *
     * @param ip      服务器IP地址
     * @param address 服务器网卡地址
     * @param time    时间
     * @return layUiAdmin响应对象
     * @author 皮锋
     * @custom.date 2021/1/10 20:37
     */
    @Operation(summary = "获取服务器详情页面服务器网速图表信息")
    @ResponseBody
    @GetMapping("/get-server-detail-page-server-network-speed-chart-info")
    @Parameters(value = {
            @Parameter(name = "ip", description = "服务器IP地址", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "address", description = "服务器网卡地址", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "time", description = "时间", in = ParameterIn.QUERY)})
    public LayUiAdminResultVo getServerDetailPageServerNetworkSpeedChartInfo(@RequestParam(name = "ip") String ip,
                                                                             @RequestParam(name = "address") String address,
                                                                             @RequestParam(name = "time", required = false) String time) {
        List<ServerDetailPageServerNetworkSpeedChartVo> networkSpeedChartVos = this.monitorServerNetcardHistoryService.getServerDetailPageServerNetworkSpeedChartInfo(ip, address, time);
        return LayUiAdminResultVo.ok(networkSpeedChartVos);
    }

}

