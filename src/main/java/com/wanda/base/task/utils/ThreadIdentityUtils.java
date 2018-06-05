package com.wanda.base.task.utils;

import java.lang.management.ManagementFactory;

/**
 * description: 
 * @author senvon
 * time : 2015年4月16日 下午5:17:50
 */
public class ThreadIdentityUtils {

    /**
     * 获得当前线程标识<br>
     * 进程号@机器名|IP地址|线程号<br>
     * 样例：5234@sh-xurong-ubuntu|xxx.xxx.xx.xx|main|1<br>
     * 代码：ManagementFactory.getRuntimeMXBean().getName()|LocalhostUtils.getLocalIpv4|Thread.currentThread().getName()|Thread.currentThread().getId()
     */
    public static String getIdentity() {
        return ManagementFactory.getRuntimeMXBean().getName() + "|" + LocalhostUtils.getLocalIpv4() + "|" + Thread.currentThread().getName() + "|" + Thread.currentThread().getId();
    }

}
