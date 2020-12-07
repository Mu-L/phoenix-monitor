package com.gitee.pifeng.server.constant;

import java.io.File;

/**
 * <p>
 * 文件名字常量
 * </p>
 *
 * @author 皮锋
 * @custom.date 2020年3月31日 下午4:02:57
 */
public final class FileNameConstants {

    /**
     * <p>
     * 私有化构造方法
     * </p>
     *
     * @author 皮锋
     * @custom.date 2020/10/27 13:26
     */
    private FileNameConstants() {
    }

    /**
     * 文件存储路径
     */
    private static final String PATH = System.getProperty("user.dir")
            + File.separator + "persistent-monitoring" + File.separator + "ini" + File.separator;

    /**
     * 应用实例池
     */
    public static final String INSTANCE_POOL = PATH + "instancePool.json";

    /**
     * 网络信息池
     */
    public static final String NET_POOL = PATH + "netPool.json";

    /**
     * 服务器内存池
     */
    public static final String MEMORY_POOL = PATH + "memoryPool.json";

    /**
     * 服务器CPU池
     */
    public static final String CPU_POOL = PATH + "cpuPool.json";

    /**
     * 服务器磁盘池
     */
    public static final String DISK_POOL = PATH + "diskPool.json";

}