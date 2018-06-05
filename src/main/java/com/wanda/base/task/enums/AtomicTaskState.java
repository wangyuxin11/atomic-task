package com.wanda.base.task.enums;

/**
 * description: 任务类型
 * @author senvon
 * time : 2015年4月17日 上午9:50:02
 */
public enum AtomicTaskState {
    /**
     * 待执行
     */
    WAITING_EXECUTE,
    /**
     * 执行中
     */
    EXECUTING,
    /**
     * 以完成
     */
    COMPLETE,
    /**
     * 不重试,任务返回明确的不重试或已经到最大次数
     */
    NOT_RETRY
}
