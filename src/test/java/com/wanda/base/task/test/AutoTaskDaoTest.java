package com.wanda.base.task.test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.wanda.base.task.dao.AutoTaskDAO;
import com.wanda.base.task.enums.AtomicTaskState;
import com.wanda.base.task.model.AutoTask;

@RunWith(org.springframework.test.context.junit4.SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring-datasource.xml"})
@Transactional
@TransactionConfiguration(defaultRollback=true)
public class AutoTaskDaoTest {

	@Autowired
	private AutoTaskDAO autoTaskDao;
	
	@Test
	public void testSelect() throws Exception{
		AutoTask task = new AutoTask();
		task.setTaskNo("senvonTest001");
		task.setTaskType("senvonTask");
		task.setTaskMethod("spring:testTask:handle");
		task.setNextExecTime(new Date());
		task.setExecExpireMinute(1);
		Map<String , Object> context = new HashMap<String , Object>();
		context.put("test", "senvon test");
		String contextJson = JSON.toJSONString(context);
		task.setContextJson(contextJson);
		
		task.setMaxExecTimes(3);
		task.setExecutedTimes(1);
		task.setTaskState(AtomicTaskState.WAITING_EXECUTE.name());
		task.setCreateTime(new Date());
		task.setUpdateTime(new Date());
		autoTaskDao.insertTask(task);
		
		AutoTask queryTask = autoTaskDao.findTaskById(task.getTaskNo(), task.getTaskType());
		Assert.assertTrue(queryTask != null);
		System.out.println(queryTask);
		Assert.assertTrue(queryTask.getTaskNo().equalsIgnoreCase(task.getTaskNo()));
		Assert.assertTrue(queryTask.getTaskType().equalsIgnoreCase(task.getTaskType()));
		
		
		task.setLastExecedTime(new Date());
		task.setCurExecutor("senvon2222");
		task.setTaskState(AtomicTaskState.COMPLETE.name());
		autoTaskDao.updateSelectiveByKey(task);
		
		AutoTask queryTask2 = autoTaskDao.findTaskById(task.getTaskNo(), task.getTaskType());
		Assert.assertTrue(queryTask2 != null);
		Assert.assertTrue(queryTask2.getCurExecutor().equalsIgnoreCase(task.getCurExecutor()));
		Assert.assertTrue(queryTask2.getTaskState().equalsIgnoreCase(task.getTaskState()));
	}
}
