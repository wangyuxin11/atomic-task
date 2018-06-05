package com.wanda.base.task.vo;

import java.util.Map;

public class TaskExecContext {
    private String taskType;
    private String taskNo;
    private Map<String, String> context;
    /**
     * 已执行次数，从0开始
     */
    private int executedTimes;
    private int maxExecTimes;
    /**
     * 是否人工触发
     */
    private boolean manual;
    /**
     * 同步传入的参数
     */
    private Object syncParams;

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getTaskNo() {
        return taskNo;
    }

    public void setTaskNo(String taskNo) {
        this.taskNo = taskNo;
    }

    public Map<String, String> getContext() {
        return context;
    }

    public void setContext(Map<String, String> context) {
        this.context = context;
    }

    public int getMaxExecTimes() {
        return maxExecTimes;
    }

    public void setMaxExecTimes(int maxExecTimes) {
        this.maxExecTimes = maxExecTimes;
    }

    /**
     * 已执行次数，从0开始
     */
    public int getExecutedTimes() {
        return executedTimes;
    }

    public void setExecutedTimes(int executedTimes) {
        this.executedTimes = executedTimes;
    }

    public boolean isManual() {
        return manual;
    }

    public void setManual(boolean manual) {
        this.manual = manual;
    }

    public Object getSyncParams() {
        return syncParams;
    }

    public void setSyncParams(Object syncParams) {
        this.syncParams = syncParams;
    }

}
