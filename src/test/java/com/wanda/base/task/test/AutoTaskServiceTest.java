package com.wanda.base.task.test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.base.task.dao.AutoTaskDAO;
import com.wanda.base.task.enums.AtomicTaskState;
import com.wanda.base.task.exception.DuplicateTaskException;
import com.wanda.base.task.model.AutoTask;
import com.wanda.base.task.vo.SyncTaskResult;

@RunWith(org.springframework.test.context.junit4.SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring-datasource.xml"})
@Transactional
@TransactionConfiguration(defaultRollback=true)
public class AutoTaskServiceTest {

	@Autowired
	private com.wanda.base.task.service.impl.AtomicTaskServiceImpl autoTaskService;
	
	@Autowired
	private AutoTaskDAO autoTaskDao;
	@Autowired
	private TestTask testTask;
	
	@After
	@Before
	public void clear() throws Exception{
		testTask.clear();
	}
	
	@Test
	public void duplicateAdd() throws Exception{
		String taskType = "senvon";
		String taskNo = "senvon001";
		String invokeMethod = "spring:testTask.handle";
		Map<String , String> context = new HashMap<String , String>();
		context.put("test", "senvon test");
		Integer firstExecDelaySeconds =1;
		Integer execExpireMinute = 3;
		Integer maxExecTimes = 3;
		autoTaskService.addAtomicTask(taskType, taskNo, invokeMethod, context, firstExecDelaySeconds, execExpireMinute, maxExecTimes);
		
		try{
			autoTaskService.addAtomicTask(taskType, taskNo, invokeMethod, context, firstExecDelaySeconds, execExpireMinute, maxExecTimes);
		}catch(Exception e){
			Assert.assertTrue(e instanceof DuplicateTaskException);
		}
	}
	
	@Test
	public void lockUnlock() throws Exception{
		String taskType = "senvon";
		String taskNo = "senvon001";
		String invokeMethod = "spring:testTask.handle";
		Map<String , String> context = new HashMap<String , String>();
		context.put("test", "senvon test");
		Integer firstExecDelaySeconds =1;
		Integer execExpireMinute = 3;
		Integer maxExecTimes = 3;
		autoTaskService.addAtomicTask(taskType, taskNo, invokeMethod, context, firstExecDelaySeconds, execExpireMinute, maxExecTimes);
		
		AutoTask task = autoTaskDao.findTaskById(taskNo, taskType);
		task.setCurExecutor("senvon123");
		task.setNextExecTime(new Date());
		task.setLastExecTime(new Date());
		autoTaskDao.updateSelectiveByKey(task);
		autoTaskService.lockTask(task);
		autoTaskDao.unlockTask(taskNo, taskType, "senvon123");
		
		AutoTask task11 = autoTaskDao.findTaskById(taskNo, taskType);
		Assert.assertTrue(StringUtils.isBlank(task11.getCurExecutor()));
	}
	
	@Test
	public void successExecute() throws Exception{
		String taskType = "senvon";
		String taskNo = "senvon001";
		String invokeMethod = "spring:testTask.handle";
		Map<String , String> context = new HashMap<String , String>();
		context.put("test", "senvon test");
		Integer firstExecDelaySeconds =1;
		Integer execExpireMinute = 3;
		Integer maxExecTimes = 3;
		autoTaskService.addAtomicTask(taskType, taskNo, invokeMethod, context, firstExecDelaySeconds, execExpireMinute, maxExecTimes);
		
		SyncTaskResult taskResult = autoTaskService.syncExecuteTask(taskType, taskNo);
		
		Assert.assertTrue(taskResult != null);
		Assert.assertTrue(taskResult.isComplete());
		Assert.assertTrue(testTask.getCount() == 1);
		
		AutoTask queryTask = autoTaskDao.findTaskById(taskNo, taskType);
		Assert.assertTrue(queryTask != null);
		Assert.assertTrue(queryTask.getTaskState().equalsIgnoreCase(AtomicTaskState.COMPLETE.name()));
	}
	
}
