package com.wanda.base.task.service;

import java.util.Map;

import com.wanda.base.task.exception.DuplicateTaskException;
import com.wanda.base.task.exception.LockingTaskFailedException;
import com.wanda.base.task.vo.SyncTaskResult;
import com.wanda.base.task.vo.TaskResult;

public interface AtomicTaskService {
    /**
     * 添加任务（任务类型+任务编码）唯一<br>
     * 回调方法
     * <pre>
     * public TaskExecResult taskCallback(TaskExecContext context) {
     *     TaskExecResult result = new TaskExecResult(false);
     *     // ......
     *     return result;
     * }
     * </pre>
     * @param taskType 任务类型
     * @param taskNo 任务编码，（任务类型+任务编码）唯一
     * @param invokeMethod 调用方法，格式spring:beanId.method,方法参数:TaskExecContext,返回TaskExecResult
     * @param context 任务上下文，数据库中以json串存储，最大4000个字符
     * @param firstExecDelaySeconds 第一次执行时间延迟，后续可能直接调用同步任务，防止被定时任务获取。仅对定时任务有用
     * @param execExpireMinute 任务超时时长，大于0。用于长时间处理中，可能执行线程已经异常。解锁后由其他线程执行。
     * @param maxExecTimes 最大执行次数，包括第一次执行。必须大于0。仅对定时任务有用
     * @throws DuplicateTaskException 唯一约束异常，（任务类型+任务编码）唯一
     */
    public void addAtomicTask(String taskType, String taskNo, String invokeMethod, Map<String, String> context, int firstExecDelaySeconds, int execExpireMinute, int maxExecTimes)
            throws DuplicateTaskException;

    /**
     * 同步执行task
     * @param taskType
     * @param taskNo
     * @return
     * @throws LockingTaskFailedException 有补偿任务或多线程调用，锁定失败
     */
    public SyncTaskResult syncExecuteTask(String taskType, String taskNo) throws LockingTaskFailedException;

    /**
     * 异步执行
     * @param taskType
     * @param taskNo
     * @return
     */
    public void asynExecuteTask(String taskType, String taskNo);

    /**
     * 获得执行结果
     * @param taskType
     * @param taskNo
     * @return
     */
    public TaskResult getAtomicTask(String taskType, String taskNo);


    /**
     * 补偿未完成的任务
     */
    public void compensateNotCompleteTask();
    
}
