package com.gitee.pifeng.monitoring.ui.business.web.controller;

import com.gitee.pifeng.monitoring.ui.business.web.service.IMonitorServerPowerSourcesService;
import com.gitee.pifeng.monitoring.ui.business.web.vo.LayUiAdminResultVo;
import com.gitee.pifeng.monitoring.ui.business.web.vo.MonitorServerPowerSourcesVo;
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
 * 服务器电池
 * </p>
 *
 * @author 皮锋
 * @custom.date 2021-01-15
 */
@Tag(name = "服务器.服务器电池")
@Controller
@RequestMapping("/monitor-server-power-sources")
public class MonitorServerPowerSourcesController {

    /**
     * 服务器电池服务类
     */
    @Autowired
    private IMonitorServerPowerSourcesService monitorServerPowerSourcesService;

    /**
     * <p>
     * 获取服务器详情页面服务器电池信息
     * </p>
     *
     * @param ip 服务器IP地址
     * @return layUiAdmin响应对象
     * @author 皮锋
     * @custom.date 2021/1/16 17:47
     */
    @Operation(summary = "获取服务器详情页面服务器电池信息")
    @ResponseBody
    @GetMapping("/get-server-detail-page-server-power-sources-info")
    @Parameters(value = {
            @Parameter(name = "ip", description = "服务器IP地址", required = true, in = ParameterIn.QUERY)})
    public LayUiAdminResultVo getServerDetailPageServerPowerSourcesInfo(@RequestParam(name = "ip") String ip) {
        List<MonitorServerPowerSourcesVo> serverPowerSourcesVos = this.monitorServerPowerSourcesService.getServerDetailPageServerPowerSourcesInfo(ip);
        return LayUiAdminResultVo.ok(serverPowerSourcesVos);
    }

    /**
     * <p>
     * 获取服务器详情页面服务器电池图表信息
     * </p>
     *
     * @param ip 服务器IP地址
     * @return layUiAdmin响应对象
     * @author 皮锋
     * @custom.date 2021/1/17 19:43
     */
    @Operation(summary = "获取服务器详情页面服务器电池图表信息")
    @ResponseBody
    @GetMapping("/get-server-detail-page-server-power-sources-chart-info")
    @Parameters(value = {
            @Parameter(name = "ip", description = "服务器IP地址", required = true, in = ParameterIn.QUERY)})
    public LayUiAdminResultVo getServerDetailPageServerPowerSourcesChartInfo(@RequestParam(name = "ip") String ip) {
        Double rCapacityPercentAvg = this.monitorServerPowerSourcesService.getRemainingCapacityPercentAvg(ip);
        return LayUiAdminResultVo.ok(rCapacityPercentAvg);
    }

}

