package com.gitee.pifeng.monitoring.ui.business.web.component;

import com.alibaba.fastjson.JSON;
import com.gitee.pifeng.monitoring.common.web.util.AccessObjectUtil;
import com.gitee.pifeng.monitoring.common.web.util.ContextUtils;
import com.gitee.pifeng.monitoring.ui.business.web.annotation.OperateLog;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorLogOperation;
import com.gitee.pifeng.monitoring.ui.business.web.service.IMonitorLogExceptionService;
import com.gitee.pifeng.monitoring.ui.business.web.service.IMonitorLogOperationService;
import com.gitee.pifeng.monitoring.ui.util.SpringSecurityUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 操作日志、异常日志切面
 * </p>
 *
 * @author 皮锋
 * @custom.date 2021/6/10 10:43
 */
@Aspect
@Component
public class OperateLogAspect {

    /**
     * 异常日志服务类
     */
    @Autowired
    private IMonitorLogExceptionService monitorLogExceptionService;

    /**
     * 操作日志服务类
     */
    @Autowired
    private IMonitorLogOperationService monitorLogOperationService;

    /**
     * <p>
     * 操作日志切入点
     * </p>
     *
     * @author 皮锋
     * @custom.date 2021/6/10 10:47
     */
    @Pointcut("@annotation(com.gitee.pifeng.monitoring.ui.business.web.annotation.OperateLog)")
    public void operateLogPointCut() {
    }

    /**
     * <p>
     * 异常日志切入点
     * </p>
     *
     * @author 皮锋
     * @custom.date 2021/6/10 10:49
     */
    @Pointcut("execution(* com.gitee.pifeng.monitoring.ui.business.web.controller..*.*(..))")
    public void exceptionLogPointCut() {
    }

    /**
     * <p>
     * 正常返回通知，记录用户操作日志，连接点正常执行完成后执行，如果连接点抛出异常，则不会执行
     * </p>
     *
     * @param joinPoint 切入点
     * @param response  返回结果
     * @author 皮锋
     * @custom.date 2021/6/10 10:55
     */
    @AfterReturning(value = "operateLogPointCut()", returning = "response")
    public void saveOperateLog(JoinPoint joinPoint, Object response) {
        HttpServletRequest request = ContextUtils.getRequest();
        // 从切面织入点处通过反射机制获取织入点处的方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 获取切入点所在的方法
        Method method = signature.getMethod();
        // 获取请求的类名
        String className = joinPoint.getTarget().getClass().getName();
        // 获取请求的方法名
        String methodName = className + "#" + method.getName();
        // 操作日志
        MonitorLogOperation.MonitorLogOperationBuilder builder = MonitorLogOperation.builder();
        // 获取操作
        OperateLog operateLog = method.getAnnotation(OperateLog.class);
        if (operateLog != null) {
            String operateModule = operateLog.operModule();
            String operateType = operateLog.operType();
            String operateDesc = operateLog.operDesc();
            // 操作模块
            builder.operModule(operateModule);
            // 操作类型
            builder.operType(operateType);
            // 操作描述
            builder.operDesc(operateDesc);
        }
        builder.reqParam(JSON.toJSONString(this.convertParamMap(request.getParameterMap())));
        builder.respParam(JSON.toJSONString(response));
        builder.userId(SpringSecurityUtils.getCurrentMonitorUserRealm().getId());
        builder.username(SpringSecurityUtils.getCurrentMonitorUserRealm().getUsrname());
        builder.operMethod(methodName);
        builder.uri(request.getRequestURI());
        builder.ip(AccessObjectUtil.getClientAddress(request));
        builder.insertTime(new Date());
        this.monitorLogOperationService.save(builder.build());
    }

    /**
     * <p>
     * 异常返回通知，用于记录异常日志，连接点抛出异常后执行
     * </p>
     *
     * @param joinPoint 切入点
     * @param e         异常信息
     * @author 皮锋
     * @custom.date 2021/6/10 10:58
     */
    @AfterThrowing(pointcut = "exceptionLogPointCut()", throwing = "e")
    public void saveExceptionLog(JoinPoint joinPoint, Throwable e) {

    }

    /**
     * <p>
     * 转换请求参数
     * </p>
     *
     * @param paramMap 请求参数
     * @return {@link Map}
     * @author 皮锋
     * @custom.date 2021/6/10 13:45
     */
    private Map<String, String> convertParamMap(Map<String, String[]> paramMap) {
        Map<String, String> rtnMap = new HashMap<>(16);
        for (String key : paramMap.keySet()) {
            rtnMap.put(key, paramMap.get(key)[0]);
        }
        return rtnMap;
    }

}
