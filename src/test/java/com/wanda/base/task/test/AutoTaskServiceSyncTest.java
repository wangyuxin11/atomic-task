package com.wanda.base.task.test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;

import com.wanda.base.task.dao.AutoTaskDAO;
import com.wanda.base.task.enums.AtomicTaskState;
import com.wanda.base.task.model.AutoTask;
import com.wanda.base.task.service.AtomicTaskService;

@RunWith(org.springframework.test.context.junit4.SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring-datasource.xml"})

public class AutoTaskServiceSyncTest {

	@Autowired
	private AtomicTaskService autoTaskService;
	
	@Autowired
	private AutoTaskDAO autoTaskDao;
	@Autowired
	private TestTask testTask;
	
	@Autowired
	private DataSource datasource;
	
	String taskType = "senvon";
	String taskNo = "senvon001";
	
	@After
	@Before
	public void delete() throws Exception{
		NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(datasource);
		String sql = "DELETE FROM AUTO_TASK WHERE TASK_NO=:taskNo AND TASK_TYPE=:taskType";
		Map<String, Object> paramMap = new HashMap<String , Object>();
		paramMap.put("taskNo", taskNo);
		paramMap.put("taskType", taskType);
		template.update(sql, paramMap);
		
		testTask.clear();
	}
	
	@Test
	public void successSyncExecute() throws Exception{
		String invokeMethod = "spring:testTask.handle";
		Map<String , String> context = new HashMap<String , String>();
		context.put("test", "senvon test");
		Integer firstExecDelaySeconds =1;
		Integer execExpireMinute = 3;
		Integer maxExecTimes = 3;
		autoTaskService.addAtomicTask(taskType, taskNo, invokeMethod, context, firstExecDelaySeconds, execExpireMinute, maxExecTimes);
		
		autoTaskService.asynExecuteTask(taskType, taskNo);
		
		Thread.sleep(3000);
		Assert.assertTrue(testTask.getCount() == 1);
		
		AutoTask queryTask = autoTaskDao.findTaskById(taskNo, taskType);
		Assert.assertTrue(queryTask != null);
		Assert.assertTrue(queryTask.getTaskState().equalsIgnoreCase(AtomicTaskState.COMPLETE.name()));
	}
	
	@Test
	public void mutilbleTest() throws Exception{
		for(int i = 0 ;i<10 ;i++){
			Thread t = new Thread(new ExecuteThread());
			t.start();
			latch.countDown();
		}
		
		Thread.sleep(3000);
		Assert.assertTrue(testTask.getCount() == 1);
		
		AutoTask queryTask = autoTaskDao.findTaskById(taskNo, taskType);
		Assert.assertTrue(queryTask != null);
		Assert.assertTrue(queryTask.getTaskState().equalsIgnoreCase(AtomicTaskState.COMPLETE.name()));
	}
	
	private CountDownLatch latch = new CountDownLatch(10);
	
	private class ExecuteThread implements Runnable{

		@Override
		public void run() {
			try {
				latch.await();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			String invokeMethod = "spring:testTask.handle";
			Map<String , String> context = new HashMap<String , String>();
			context.put("test", "senvon test");
			Integer firstExecDelaySeconds =1;
			Integer execExpireMinute = 3;
			Integer maxExecTimes = 3;
			
			try{
				autoTaskService.addAtomicTask(taskType, taskNo, invokeMethod, context, firstExecDelaySeconds, execExpireMinute, maxExecTimes);
				autoTaskService.asynExecuteTask(taskType, taskNo);
			}catch(Exception e){
				
			}
		}
	}
}
