package com.wanda.base.task.model;

import java.io.Serializable;
import java.util.Date;

public class AutoTask implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * VARCHAR(64) 必填<br>
     * 任务编号
     */
    private String taskNo;

    /**
     * VARCHAR(30) 必填<br>
     * 任务分类
     */
    private String taskType;
    
    /**
     * VARCHAR(64) 必填<br>
     * 调用方法,spring:beanId.method
	         参数:TaskExecContext
	        返回:TaskResult
     */
    private String taskMethod;

    /**
     * TIMESTAMP(11,6) 必填<br>
     * 下次执行时间
     */
    private Date nextExecTime;

    /**
     * VARCHAR(128)<br>
     * 当前执行者,执行完成会清除
     */
    private String curExecutor;

    /**
     * TIMESTAMP(11,6)<br>
     * 最近执行时间
     */
    private Date lastExecTime;

    /**
     * TIMESTAMP(11,6)<br>
     * 最近结束时间
     */
    private Date lastExecedTime;

    /**
     * VARCHAR(128)<br>
     * 最近执行者
     */
    private String lastExecutor;

    /**
     * DECIMAL(8) 必填<br>
     * 执行超时时长(分钟)
     */
    private Integer execExpireMinute;

    /**
     * DECIMAL(8) 必填<br>
     * 已执行次数
     */
    private Integer executedTimes;

    /**
     * DECIMAL(8) 必填<br>
     * 最大执行次数
     */
    private Integer maxExecTimes;

    /**
     * VARCHAR(4000) 必填<br>
     * 任务上下文
     */
    private String contextJson;

    /**
     * VARCHAR(4000)<br>
     * 错误消息
     */
    private String execMsg;

    /**
     * VARCHAR(30)<br>
     * 任务状态
     */
    private String taskState;

    /**
     * TIMESTAMP(11,6) 必填<br>
     * 创建时间
     */
    private Date createTime;

    /**
     * TIMESTAMP(11,6) 必填<br>
     * 更新时间
     */
    private Date updateTime;

    /**
     * VARCHAR(64) 必填<br>
     * 获得 调用方法,spring:beanId.method
参数:TaskExecContext
返回:TaskResult
     */
    public String getTaskMethod() {
        return taskMethod;
    }

    /**
     * VARCHAR(64) 必填<br>
     * 设置 调用方法,spring:beanId.method
参数:TaskExecContext
返回:TaskResult
     */
    public void setTaskMethod(String taskMethod) {
        this.taskMethod = taskMethod == null ? null : taskMethod.trim();
    }

    /**
     * TIMESTAMP(11,6) 必填<br>
     * 获得 下次执行时间
     */
    public Date getNextExecTime() {
        return nextExecTime;
    }

    /**
     * TIMESTAMP(11,6) 必填<br>
     * 设置 下次执行时间
     */
    public void setNextExecTime(Date nextExecTime) {
        this.nextExecTime = nextExecTime;
    }

    /**
     * VARCHAR(128)<br>
     * 获得 当前执行者,执行完成会清除
     */
    public String getCurExecutor() {
        return curExecutor;
    }

    /**
     * VARCHAR(128)<br>
     * 设置 当前执行者,执行完成会清除
     */
    public void setCurExecutor(String curExecutor) {
        this.curExecutor = curExecutor == null ? null : curExecutor.trim();
    }

    /**
     * TIMESTAMP(11,6)<br>
     * 获得 最近执行时间
     */
    public Date getLastExecTime() {
        return lastExecTime;
    }

    /**
     * TIMESTAMP(11,6)<br>
     * 设置 最近执行时间
     */
    public void setLastExecTime(Date lastExecTime) {
        this.lastExecTime = lastExecTime;
    }

    /**
     * TIMESTAMP(11,6)<br>
     * 获得 最近结束时间
     */
    public Date getLastExecedTime() {
        return lastExecedTime;
    }

    /**
     * TIMESTAMP(11,6)<br>
     * 设置 最近结束时间
     */
    public void setLastExecedTime(Date lastExecedTime) {
        this.lastExecedTime = lastExecedTime;
    }

    /**
     * VARCHAR(128)<br>
     * 获得 最近执行者
     */
    public String getLastExecutor() {
        return lastExecutor;
    }

    /**
     * VARCHAR(128)<br>
     * 设置 最近执行者
     */
    public void setLastExecutor(String lastExecutor) {
        this.lastExecutor = lastExecutor == null ? null : lastExecutor.trim();
    }

    /**
     * DECIMAL(8) 必填<br>
     * 获得 执行超时时长(分钟)
     */
    public Integer getExecExpireMinute() {
        return execExpireMinute;
    }

    /**
     * DECIMAL(8) 必填<br>
     * 设置 执行超时时长(分钟)
     */
    public void setExecExpireMinute(Integer execExpireMinute) {
        this.execExpireMinute = execExpireMinute;
    }

    /**
     * DECIMAL(8) 必填<br>
     * 获得 已执行次数
     */
    public Integer getExecutedTimes() {
        return executedTimes;
    }

    /**
     * DECIMAL(8) 必填<br>
     * 设置 已执行次数
     */
    public void setExecutedTimes(Integer executedTimes) {
        this.executedTimes = executedTimes;
    }

    /**
     * DECIMAL(8) 必填<br>
     * 获得 最大执行次数
     */
    public Integer getMaxExecTimes() {
        return maxExecTimes;
    }

    /**
     * DECIMAL(8) 必填<br>
     * 设置 最大执行次数
     */
    public void setMaxExecTimes(Integer maxExecTimes) {
        this.maxExecTimes = maxExecTimes;
    }

    /**
     * VARCHAR(4000) 必填<br>
     * 获得 任务上下文
     */
    public String getContextJson() {
        return contextJson;
    }

    /**
     * VARCHAR(4000) 必填<br>
     * 设置 任务上下文
     */
    public void setContextJson(String contextJson) {
        this.contextJson = contextJson == null ? null : contextJson.trim();
    }

    /**
     * VARCHAR(4000)<br>
     * 获得 错误消息
     */
    public String getExecMsg() {
        return execMsg;
    }

    /**
     * VARCHAR(4000)<br>
     * 设置 错误消息
     */
    public void setExecMsg(String execMsg) {
        this.execMsg = execMsg == null ? null : execMsg.trim();
    }

    /**
     * VARCHAR(30)<br>
     * 获得 任务状态
     */
    public String getTaskState() {
        return taskState;
    }

    /**
     * VARCHAR(30)<br>
     * 设置 任务状态
     */
    public void setTaskState(String taskState) {
        this.taskState = taskState == null ? null : taskState.trim();
    }

    /**
     * TIMESTAMP(11,6) 必填<br>
     * 获得 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * TIMESTAMP(11,6) 必填<br>
     * 设置 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * TIMESTAMP(11,6) 必填<br>
     * 获得 更新时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * TIMESTAMP(11,6) 必填<br>
     * 设置 更新时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
    
    /**
     * VARCHAR(64) 必填<br>
     * 获得 任务编号
     */
    public String getTaskNo() {
        return taskNo;
    }

    /**
     * VARCHAR(64) 必填<br>
     * 设置 任务编号
     */
    public void setTaskNo(String taskNo) {
        this.taskNo = taskNo == null ? null : taskNo.trim();
    }

    /**
     * VARCHAR(30) 必填<br>
     * 获得 任务分类
     */
    public String getTaskType() {
        return taskType;
    }

    /**
     * VARCHAR(30) 必填<br>
     * 设置 任务分类
     */
    public void setTaskType(String taskType) {
        this.taskType = taskType == null ? null : taskType.trim();
    }


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AutoTask [taskNo=");
		builder.append(taskNo);
		builder.append(", taskType=");
		builder.append(taskType);
		builder.append(", taskMethod=");
		builder.append(taskMethod);
		builder.append(", nextExecTime=");
		builder.append(nextExecTime);
		builder.append(", curExecutor=");
		builder.append(curExecutor);
		builder.append(", lastExecTime=");
		builder.append(lastExecTime);
		builder.append(", lastExecedTime=");
		builder.append(lastExecedTime);
		builder.append(", lastExecutor=");
		builder.append(lastExecutor);
		builder.append(", execExpireMinute=");
		builder.append(execExpireMinute);
		builder.append(", executedTimes=");
		builder.append(executedTimes);
		builder.append(", maxExecTimes=");
		builder.append(maxExecTimes);
		builder.append(", contextJson=");
		builder.append(contextJson);
		builder.append(", execMsg=");
		builder.append(execMsg);
		builder.append(", taskState=");
		builder.append(taskState);
		builder.append(", createTime=");
		builder.append(createTime);
		builder.append(", updateTime=");
		builder.append(updateTime);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((contextJson == null) ? 0 : contextJson.hashCode());
		result = prime * result
				+ ((createTime == null) ? 0 : createTime.hashCode());
		result = prime * result
				+ ((curExecutor == null) ? 0 : curExecutor.hashCode());
		result = prime
				* result
				+ ((execExpireMinute == null) ? 0 : execExpireMinute.hashCode());
		result = prime * result + ((execMsg == null) ? 0 : execMsg.hashCode());
		result = prime * result
				+ ((executedTimes == null) ? 0 : executedTimes.hashCode());
		result = prime * result
				+ ((lastExecTime == null) ? 0 : lastExecTime.hashCode());
		result = prime * result
				+ ((lastExecedTime == null) ? 0 : lastExecedTime.hashCode());
		result = prime * result
				+ ((lastExecutor == null) ? 0 : lastExecutor.hashCode());
		result = prime * result
				+ ((maxExecTimes == null) ? 0 : maxExecTimes.hashCode());
		result = prime * result
				+ ((nextExecTime == null) ? 0 : nextExecTime.hashCode());
		result = prime * result
				+ ((taskMethod == null) ? 0 : taskMethod.hashCode());
		result = prime * result + ((taskNo == null) ? 0 : taskNo.hashCode());
		result = prime * result
				+ ((taskState == null) ? 0 : taskState.hashCode());
		result = prime * result
				+ ((taskType == null) ? 0 : taskType.hashCode());
		result = prime * result
				+ ((updateTime == null) ? 0 : updateTime.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		AutoTask other = (AutoTask) obj;
		if (contextJson == null) {
			if (other.contextJson != null)
				return false;
		} else if (!contextJson.equals(other.contextJson))
			return false;
		if (createTime == null) {
			if (other.createTime != null)
				return false;
		} else if (!createTime.equals(other.createTime))
			return false;
		if (curExecutor == null) {
			if (other.curExecutor != null)
				return false;
		} else if (!curExecutor.equals(other.curExecutor))
			return false;
		if (execExpireMinute == null) {
			if (other.execExpireMinute != null)
				return false;
		} else if (!execExpireMinute.equals(other.execExpireMinute))
			return false;
		if (execMsg == null) {
			if (other.execMsg != null)
				return false;
		} else if (!execMsg.equals(other.execMsg))
			return false;
		if (executedTimes == null) {
			if (other.executedTimes != null)
				return false;
		} else if (!executedTimes.equals(other.executedTimes))
			return false;
		if (lastExecTime == null) {
			if (other.lastExecTime != null)
				return false;
		} else if (!lastExecTime.equals(other.lastExecTime))
			return false;
		if (lastExecedTime == null) {
			if (other.lastExecedTime != null)
				return false;
		} else if (!lastExecedTime.equals(other.lastExecedTime))
			return false;
		if (lastExecutor == null) {
			if (other.lastExecutor != null)
				return false;
		} else if (!lastExecutor.equals(other.lastExecutor))
			return false;
		if (maxExecTimes == null) {
			if (other.maxExecTimes != null)
				return false;
		} else if (!maxExecTimes.equals(other.maxExecTimes))
			return false;
		if (nextExecTime == null) {
			if (other.nextExecTime != null)
				return false;
		} else if (!nextExecTime.equals(other.nextExecTime))
			return false;
		if (taskMethod == null) {
			if (other.taskMethod != null)
				return false;
		} else if (!taskMethod.equals(other.taskMethod))
			return false;
		if (taskNo == null) {
			if (other.taskNo != null)
				return false;
		} else if (!taskNo.equals(other.taskNo))
			return false;
		if (taskState == null) {
			if (other.taskState != null)
				return false;
		} else if (!taskState.equals(other.taskState))
			return false;
		if (taskType == null) {
			if (other.taskType != null)
				return false;
		} else if (!taskType.equals(other.taskType))
			return false;
		if (updateTime == null) {
			if (other.updateTime != null)
				return false;
		} else if (!updateTime.equals(other.updateTime))
			return false;
		return true;
	}

    
}