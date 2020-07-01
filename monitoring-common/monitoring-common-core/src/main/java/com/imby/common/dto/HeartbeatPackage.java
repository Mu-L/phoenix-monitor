package com.imby.common.dto;

import com.imby.common.abs.AbstractInstanceBean;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 心跳包传输层对象
 * </p>
 *
 * @author 皮锋
 * @custom.date 2020年3月4日 下午12:20:06
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class HeartbeatPackage extends AbstractInstanceBean implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -1227833279912325068L;

    /**
     * ID
     */
    private String id;

    /**
     * 时间
     */
    private Date dateTime;

    /**
     * 心跳频率
     */
    private Long rate;

}
