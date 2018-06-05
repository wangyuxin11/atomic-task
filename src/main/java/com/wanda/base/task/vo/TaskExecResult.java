package com.wanda.base.task.vo;

import java.util.Date;

/**
 * description: 
 * @author senvon
 * time : 2015年4月17日 上午10:43:35
 */
public class TaskExecResult {
    private boolean complete;
    private String errorMsg;
    private Object syncResult;
    /**
     * 是否不用重试，当出现需要人工修复的错误时，返回
     */
    private Boolean isNotRetry;
    /**
     * 下次执行时间,可为null
     */
    private Date nextExecuteTime;
    /**
     * 增加最大执行次数，可为null
     */
    private Integer incrMaxExecTimes;

    public TaskExecResult(boolean complete) {
        this.complete = complete;
    }

    public TaskExecResult(boolean complete, String errorMsg) {
        this.complete = complete;
        this.errorMsg = errorMsg;
    }

    public TaskExecResult(boolean complete, String errorMsg, Boolean isNotRetry) {
        this.complete = complete;
        this.errorMsg = errorMsg;
        this.isNotRetry = isNotRetry;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public Object getSyncResult() {
        return syncResult;
    }

    public void setSyncResult(Object syncResult) {
        this.syncResult = syncResult;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Date getNextExecuteTime() {
        return nextExecuteTime;
    }

    public void setNextExecuteTime(Date nextExecuteTime) {
        this.nextExecuteTime = nextExecuteTime;
    }

    public Integer getIncrMaxExecTimes() {
        return incrMaxExecTimes;
    }

    public void setIncrMaxExecTimes(Integer incrMaxExecTimes) {
        this.incrMaxExecTimes = incrMaxExecTimes;
    }

    public Boolean getIsNotRetry() {
        return isNotRetry;
    }

    public void setIsNotRetry(Boolean isNotRetry) {
        this.isNotRetry = isNotRetry;
    }

}
