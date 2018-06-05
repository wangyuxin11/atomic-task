package com.wanda.base.task.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wanda.base.task.vo.TaskExecContext;
import com.wanda.base.task.vo.TaskExecResult;

public class TestTask {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private Integer count = 0;
	
	public TaskExecResult handle(TaskExecContext context){
		logger.info("==========the test task has been invoked=={}============" , context.getContext().get("test"));
		count ++;
		return new TaskExecResult(true);
	}
	
	public void clear(){
		count = 0;
	}
	
	public Integer getCount(){
		return count;
	}
}
