package com.gitee.pifeng.monitoring.ui.business.web.controller;


import com.gitee.pifeng.monitoring.ui.business.web.service.IMonitorJvmMemoryHistoryService;
import com.gitee.pifeng.monitoring.ui.business.web.vo.InstanceDetailPageJvmMemoryChartVo;
import com.gitee.pifeng.monitoring.ui.business.web.vo.LayUiAdminResultVo;
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
 * java虚拟机内存历史记录
 * </p>
 *
 * @author 皮锋
 * @custom.date 2021-01-24
 */
@Tag(name = "应用程序.java虚拟机内存历史记录")
@Controller
@RequestMapping("/monitor-jvm-memory-history")
public class MonitorJvmMemoryHistoryController {

    /**
     * java虚拟机内存历史记录服务类
     */
    @Autowired
    private IMonitorJvmMemoryHistoryService monitorJvmMemoryHistoryService;

    /**
     * <p>
     * 获取应用实例详情页面java虚拟机内存图表信息
     * </p>
     *
     * @param instanceId 应用实例ID
     * @param memoryType 内存类型
     * @param time       时间
     * @return layUiAdmin响应对象
     * @author 皮锋
     * @custom.date 2020/10/14 11:52
     */
    @Operation(summary = "获取应用实例详情页面java虚拟机内存图表信息")
    @ResponseBody
    @GetMapping("/get-instance-detail-page-jvm-memory-chart-info")
    @Parameters(value = {
            @Parameter(name = "instanceId", description = "应用实例ID", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "memoryType", description = "内存类型", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "time", description = "时间", required = true, in = ParameterIn.QUERY)})
    public LayUiAdminResultVo getInstanceDetailPageJvmMemoryChartInfo(@RequestParam(name = "instanceId") String instanceId,
                                                                      @RequestParam(name = "memoryType") String memoryType,
                                                                      @RequestParam(name = "time") String time) {
        List<InstanceDetailPageJvmMemoryChartVo> monitorJvmMemoryChartVos = this.monitorJvmMemoryHistoryService.getInstanceDetailPageJvmMemoryChartInfo(instanceId, memoryType, time);
        return LayUiAdminResultVo.ok(monitorJvmMemoryChartVos);
    }

}

