package com.transfar.server.business.server.core;

import com.transfar.common.inf.ISuper;
import com.transfar.server.business.server.domain.Instance;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * 应用实例池，维护哪些应用在线，哪些应用离线
 * </p>
 *
 * @author 皮锋
 * @custom.date 2020年3月9日 上午10:53:52
 */
@SuppressWarnings("serial")
@Component
public class InstancePool extends ConcurrentHashMap<String, Instance> implements ISuper {
}
