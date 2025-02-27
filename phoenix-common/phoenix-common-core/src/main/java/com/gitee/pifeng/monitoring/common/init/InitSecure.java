package com.gitee.pifeng.monitoring.common.init;

import cn.hutool.setting.dialect.Props;
import com.gitee.pifeng.monitoring.common.exception.NotFoundConfigFileException;
import com.gitee.pifeng.monitoring.common.util.DirUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * <p>
 * 初始化加解密配置
 * </p>
 *
 * @author 皮锋
 * @custom.date 2021/11/22 12:18
 */
@Slf4j
public class InitSecure {

    /**
     * 加解密配置属性
     */
    private static final Props PROPS = init();

    /**
     * <p>
     * 读取“monitoring-secure.properties”文件
     * </p>
     * 当前工作目录/config/ &gt; 当前工作目录/ &gt; classpath:/config/ &gt; classpath:/
     *
     * @return 加解密配置属性
     * @author 皮锋
     * @custom.date 2021/11/22 12:37
     */
    private static Props init() {
        Props props;
        try {
            String filePath;
            try {
                // 获取Jar同级目录
                filePath = DirUtils.getJarDirectory() + File.separator + "config" + File.separator + "monitoring-secure.properties";
            } catch (Exception e) {
                filePath = "config" + File.separator + "monitoring-secure.properties";
            }
            // 从文件路径相对于当前工作目录下的config路径读取
            props = new Props(new File(filePath), StandardCharsets.UTF_8);
        } catch (Exception e1) {
            try {
                String filePath;
                try {
                    // 获取Jar同级目录
                    filePath = DirUtils.getJarDirectory() + File.separator + "monitoring-secure.properties";
                } catch (Exception e) {
                    filePath = "monitoring-secure.properties";
                }
                // 从文件文件路径相对于当前工作目录读取
                props = new Props(new File(filePath), StandardCharsets.UTF_8);
            } catch (Exception e2) {
                try {
                    // 从 classpath:/config 路径下读取
                    props = new Props("config/monitoring-secure.properties", StandardCharsets.UTF_8);
                } catch (Exception e3) {
                    try {
                        // 从 classpath:/ 路径下读取
                        props = new Props("monitoring-secure.properties", StandardCharsets.UTF_8);
                    } catch (Exception e4) {
                        throw new NotFoundConfigFileException("监控程序找不到监控加解密配置文件！");
                    }
                }
            }
        }
        // 加解密类型
        String secretType = props.getStr("secret.type");
        log.info("初始化加解密配置成功！加解密类型：{}", StringUtils.isNotBlank(secretType) ? secretType : "无");
        return props;
    }

    /**
     * 加解密类型
     */
    public static final String SECRET_TYPE = PROPS.getStr("secret.type");

    /**
     * DES密钥
     */
    public static final String SECRET_KEY_DES = PROPS.getStr("secret.key.des");

    /**
     * AES密钥
     */
    public static final String SECRET_KEY_AES = PROPS.getStr("secret.key.aes");

    /**
     * 国密SM4密钥
     */
    public static final String SECRET_KEY_SM4 = PROPS.getStr("secret.key.sm4");

}
