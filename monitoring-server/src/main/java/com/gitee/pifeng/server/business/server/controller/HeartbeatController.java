package com.gitee.pifeng.server.business.server.controller;

import com.alibaba.fastjson.JSON;
import com.gitee.pifeng.common.domain.Result;
import com.gitee.pifeng.common.dto.BaseResponsePackage;
import com.gitee.pifeng.common.dto.HeartbeatPackage;
import com.gitee.pifeng.server.business.server.core.PackageConstructor;
import com.gitee.pifeng.server.business.server.service.IHeartbeatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 心跳包控制器
 * </p>
 *
 * @author 皮锋
 * @custom.date 2020年3月4日 下午12:16:06
 */
@RestController
@RequestMapping("/heartbeat")
@Api(tags = "信息包.心跳包")
@Slf4j
public class HeartbeatController {

    /**
     * 心跳服务接口
     */
    @Autowired
    private IHeartbeatService heartbeatService;

    /**
     * <p>
     * 监控服务端程序接收监控代理端程序或者监控客户端程序发的心跳包，并返回结果
     * </p>
     *
     * @param request 请求参数
     * @return {@link BaseResponsePackage}
     * @author 皮锋
     * @custom.date 2020年3月4日 下午12:27:47
     */
    @ApiOperation(value = "接收和响应监控代理端程序或者监控客户端程序发的心跳包", notes = "接收心跳包")
    @PostMapping("/accept-heartbeat-package")
    public BaseResponsePackage acceptHeartbeatPackage(@RequestBody String request) {
        log.debug("收到心跳包：{}", request);
        try {
            HeartbeatPackage heartbeatPackage = JSON.parseObject(request, HeartbeatPackage.class);
            // 返回结果
            Result result = this.heartbeatService.dealHeartbeatPackage(heartbeatPackage);
            return new PackageConstructor().structureBaseResponsePackage(result);
        } catch (Exception e) {
            log.error("处理心跳包异常！", e);
            return new PackageConstructor().structureBaseResponsePackage(Result.builder().isSuccess(false).msg(e.getMessage()).build());
        }
    }

}