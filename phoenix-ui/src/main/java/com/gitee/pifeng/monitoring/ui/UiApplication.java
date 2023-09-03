package com.gitee.pifeng.monitoring.ui;

import com.gitee.pifeng.monitoring.common.web.core.CustomizationBeanHandler;
import com.gitee.pifeng.monitoring.common.web.core.UniqueBeanNameGenerator;
import com.gitee.pifeng.monitoring.ui.thirdauth.common.property.ThirdAuthProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.stereotype.Indexed;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * <p>
 * 本SpringApplication类提供了一个方便的方式来引导该从Spring应用程序的main()方法开始。<br>
 * 在许多情况下，您可以委托给静态SpringApplication.run方法。
 * </p>
 *
 * @author 皮锋
 * @custom.date 2021年3月29日 下午19:13:26
 */
@Indexed
@SpringBootApplication
@EnableConfigurationProperties(ThirdAuthProperties.class)
@ComponentScan(nameGenerator = UniqueBeanNameGenerator.class)
@EnableTransactionManagement
@EnableRetry
public class UiApplication extends CustomizationBeanHandler {

    public static void main(String[] args) {
        SpringApplication.run(UiApplication.class, args);
    }

}
