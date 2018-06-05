package com.wanda.base.task.service.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
//import com.wanda.base.logTrace.wrapper.InheritTraceNoRunableWrapper;
//import com.wanda.base.logTrace.wrapper.TraceNoRunableWrapper;
import com.wanda.base.task.dao.AutoTaskDAO;
import com.wanda.base.task.enums.AtomicTaskState;
import com.wanda.base.task.exception.DuplicateTaskException;
import com.wanda.base.task.exception.LockingTaskFailedException;
import com.wanda.base.task.model.AutoTask;
import com.wanda.base.task.service.AtomicTaskService;
import com.wanda.base.task.utils.PrefixThreadFactory;
import com.wanda.base.task.utils.SpringCallbackUtils;
import com.wanda.base.task.utils.StringByteUtils;
import com.wanda.base.task.utils.ThreadIdentityUtils;
import com.wanda.base.task.vo.SyncTaskResult;
import com.wanda.base.task.vo.TaskExecContext;
import com.wanda.base.task.vo.TaskExecResult;
import com.wanda.base.task.vo.TaskResult;

/**
 * description:
 * 
 * @author senvon time : 2015年4月17日 上午10:47:51
 */
@Service("atomicTaskService")
public class AtomicTaskServiceImpl
		implements AtomicTaskService, ApplicationContextAware, InitializingBean, DisposableBean {
	private static final Logger logger = LoggerFactory.getLogger(AtomicTaskServiceImpl.class);

	private ApplicationContext applicationContext;

	@Autowired
	private AutoTaskDAO taskDao;
	private int asynThreadCount = 50;
	private int compensateThreadCount = 5;

	private ThreadPoolExecutor asynExecutorService;
	private ThreadPoolExecutor compensateExecutorService;

	public void setAsynThreadCount(int asynThreadCount) {
		this.asynThreadCount = asynThreadCount;
	}

	public void setCompensateThreadCount(int compensateThreadCount) {
		this.compensateThreadCount = compensateThreadCount;
	}

	@Override
	public void addAtomicTask(String taskType, String taskNo, String invokeMethod, Map<String, String> context,
			int firstExecDelaySeconds, int execExpireMinute, int maxExecTimes) throws DuplicateTaskException {
		Assert.notNull(taskType, "taskType不能为null");
		Assert.hasLength(taskNo, "taskNo不能为空");
		Assert.hasLength(invokeMethod, "invokeMethod不能为空");
		if (!SpringCallbackUtils.isBeanAndMethodExist(applicationContext, invokeMethod, TaskExecResult.class,
				TaskExecContext.class)) {
			throw new IllegalArgumentException(invokeMethod + "不存在");
		}
		Assert.isTrue(firstExecDelaySeconds > 0, "firstExecDelaySeconds必须大于0");
		Assert.isTrue(execExpireMinute > 0, "execExpireMinute必须大于0");
		Assert.isTrue(maxExecTimes > 0, "maxExecTimes必须大于0");
		AutoTask task = new AutoTask();
		task.setTaskType(taskType);
		task.setTaskNo(taskNo);
		task.setTaskMethod(invokeMethod);
		task.setNextExecTime(DateUtils.addSeconds(new Date(), firstExecDelaySeconds));
		task.setExecExpireMinute(execExpireMinute);
		task.setExecutedTimes(0);
		task.setMaxExecTimes(maxExecTimes);
		if (context != null) {
			task.setContextJson(JSON.toJSONString(context));
		} else {
			task.setContextJson("{}");
		}
		task.setTaskState(AtomicTaskState.WAITING_EXECUTE.name());
		task.setCreateTime(new Date());
		task.setUpdateTime(new Date());
		try {
			taskDao.insertTask(task);
		} catch (DuplicateKeyException e) {
			throw new DuplicateTaskException("taskType:" + taskType + ",taskNo:" + taskNo + " already exist", e);
		}
	}

	@Override
	public SyncTaskResult syncExecuteTask(String taskType, String taskNo) throws LockingTaskFailedException {
		return innerSyncExecuteTask(taskType, taskNo, null, false);
	}

	private SyncTaskResult innerSyncExecuteTask(String taskType, String taskNo, Object syncParams, boolean manual)
			throws LockingTaskFailedException {
		/*
		 * AtomicTaskKey atomicTaskKey = new AtomicTaskKey();
		 * atomicTaskKey.setTaskType(taskType); atomicTaskKey.setTaskNo(taskNo);
		 * AtomicTask atomicTask = taskDaoExt.selectByPrimaryKey(atomicTaskKey);
		 */
		AutoTask atomicTask = taskDao.findTaskById(taskNo, taskType);
		if (atomicTask == null) {
			logger.error("task taskType[{}],taskNo[{}] not found ", taskType, taskNo);
			throw new IllegalArgumentException("task not exist taskType:" + taskType + ", taskNo:" + taskNo);
		}
		if (!atomicTask.getTaskState().equals(AtomicTaskState.COMPLETE.name())) {
			String locker = lockTask(atomicTask);
			if (locker != null) {
				logger.info("locked task taskType[{}],taskNo[{}] by " + locker, atomicTask.getTaskType(),
						atomicTask.getTaskNo());
				try {
					// 调用方法
					TaskExecContext context = new TaskExecContext();
					context.setTaskType(taskType);
					context.setTaskNo(taskNo);
					context.setManual(manual);
					context.setSyncParams(syncParams);
					context.setContext(toMapString(atomicTask));
					context.setExecutedTimes(atomicTask.getExecutedTimes());
					context.setMaxExecTimes(atomicTask.getMaxExecTimes());
					TaskExecResult execResult = SpringCallbackUtils.invokeBeanMethod(applicationContext,
							atomicTask.getTaskMethod(), TaskExecResult.class, new Object[] { context },
							TaskExecContext.class);
					// 更新状态
					AutoTask toUpdate = new AutoTask();
					toUpdate.setTaskType(taskType);
					toUpdate.setTaskNo(taskNo);
					toUpdate.setExecMsg(
							StringByteUtils.subUtf8String(StringUtils.defaultString(execResult.getErrorMsg()), 4000));
					if (execResult.isComplete()) {
						toUpdate.setTaskState(AtomicTaskState.COMPLETE.name());
					} else if (Boolean.TRUE.equals(execResult.getIsNotRetry())
							|| isReachMaxTimes(atomicTask, execResult)) {
						toUpdate.setTaskState(AtomicTaskState.NOT_RETRY.name());
					} else {
						toUpdate.setTaskState(AtomicTaskState.WAITING_EXECUTE.name());
					}
					if (execResult.getNextExecuteTime() != null) {
						toUpdate.setNextExecTime(execResult.getNextExecuteTime());
					} else {
						toUpdate.setNextExecTime(getNextExecTime(atomicTask.getExecutedTimes() + 1));
					}
					if (execResult.getIncrMaxExecTimes() != null) {
						toUpdate.setMaxExecTimes(atomicTask.getMaxExecTimes() + execResult.getIncrMaxExecTimes());
					}
					toUpdate.setLastExecedTime(new Date());
					toUpdate.setUpdateTime(new Date());
					// taskDaoExt.updateByPrimaryKeySelective(toUpdate);
					taskDao.updateSelectiveByKey(toUpdate);
					// 构建结果
					// AtomicTask execedTask =
					// taskDaoExt.selectByPrimaryKey(atomicTaskKey);
					AutoTask execedTask = taskDao.findTaskById(taskNo, taskType);
					SyncTaskResult result = new SyncTaskResult(execResult.isComplete(), toMapString(execedTask),
							execResult.getSyncResult());
					logger.info("executed task taskType[{}],taskNo[{}] isComplete[{}] message[{}]",
							new Object[] { atomicTask.getTaskType(), atomicTask.getTaskNo(), execResult.isComplete(),
									execResult.getErrorMsg() });
					return result;
				} catch (Exception e) {
					Throwable loge = e;
					if (e instanceof InvocationTargetException) {
						InvocationTargetException ie = (InvocationTargetException) e;
						if (ie.getTargetException() != null) {
							loge = ie.getTargetException();
						}
					}
					logger.error("executed task taskType[{}],taskNo[{}] exception, " + loge.getMessage(),
							new Object[] { atomicTask.getTaskType(), atomicTask.getTaskNo(), loge });
					// 更新状态
					AutoTask toUpdate = new AutoTask();
					toUpdate.setTaskType(taskType);
					toUpdate.setTaskNo(taskNo);
					toUpdate.setTaskState(AtomicTaskState.WAITING_EXECUTE.name());
					if (atomicTask.getExecutedTimes() + 1 >= atomicTask.getMaxExecTimes()) {
						toUpdate.setTaskState(AtomicTaskState.NOT_RETRY.name());
					}
					StringWriter sw = new StringWriter();
					if (e.getCause() != null && e instanceof InvocationTargetException) {
						e.getCause().printStackTrace(new PrintWriter(sw));
					} else {
						e.printStackTrace(new PrintWriter(sw));
					}
					toUpdate.setExecMsg(StringByteUtils.subUtf8String(sw.toString(), 4000));
					toUpdate.setNextExecTime(getNextExecTime(atomicTask.getExecutedTimes() + 1));
					toUpdate.setLastExecedTime(new Date());
					toUpdate.setUpdateTime(new Date());
					// taskDaoExt.updateByPrimaryKeySelective(toUpdate);
					taskDao.updateSelectiveByKey(toUpdate);
				} finally {
					// taskDaoExt.unlockTask(atomicTask.getTaskType(),
					// atomicTask.getTaskNo(), locker);
					taskDao.unlockTask(atomicTask.getTaskType(), atomicTask.getTaskNo(), locker);
				}
			} else {
				logger.info("to lock task taskType[{}],taskNo[{}] failed", atomicTask.getTaskType(),
						atomicTask.getTaskNo());
				throw new LockingTaskFailedException("locking taskType:" + taskType + ",taskNo:" + taskNo + " failed");
			}
		}
		// 构建结果
		/*
		 * AtomicTask execedTask = taskDaoExt.selectByPrimaryKey(atomicTaskKey);
		 */
		AutoTask execedTask = taskDao.findTaskById(taskNo, taskType);
		SyncTaskResult result = new SyncTaskResult(AtomicTaskState.COMPLETE.name().equals(execedTask.getTaskState()),
				toMapString(execedTask), null);
		return result;
	}

	private boolean isReachMaxTimes(AutoTask atomicTask, TaskExecResult execResult) {
		return atomicTask.getExecutedTimes() + 1 >= atomicTask.getMaxExecTimes()
				+ (execResult.getIncrMaxExecTimes() != null ? execResult.getIncrMaxExecTimes() : 0);
	}

	private Date getNextExecTime(int execTimes) {
		int delaySeconds = (int) Math.pow(5, execTimes);
		// 最大四个小时
		delaySeconds = (int) Math.min(delaySeconds, TimeUnit.HOURS.toSeconds(4));
		return DateUtils.addSeconds(new Date(), delaySeconds);
	}

	public String lockTask(AutoTask atomicTask) {
		logger.info("to lock task taskType[{}],taskNo[{}],taskState[{}],curExecTime[{}],curExecutor[{}]",
				new Object[] { atomicTask.getTaskType(), atomicTask.getTaskNo(), atomicTask.getTaskState(),
						atomicTask.getLastExecTime(), atomicTask.getCurExecutor() });
		if (StringUtils.isEmpty(atomicTask.getCurExecutor())) {
			// 没有执行者
			AutoTask toUpdate = new AutoTask();
			toUpdate.setLastExecTime(new Date());
			toUpdate.setCurExecutor(ThreadIdentityUtils.getIdentity());
			toUpdate.setExecutedTimes(atomicTask.getExecutedTimes() + 1);
			toUpdate.setTaskState(AtomicTaskState.EXECUTING.name());
			toUpdate.setLastExecutor(toUpdate.getCurExecutor());
			toUpdate.setUpdateTime(new Date());
			toUpdate.setTaskNo(atomicTask.getTaskNo());
			toUpdate.setTaskType(atomicTask.getTaskType());
			/*
			 * AtomicTaskExample where = new AtomicTaskExample(); Criteria
			 * criteria = where.createCriteria();
			 * criteria.andTaskTypeEqualTo(atomicTask.getTaskType());
			 * criteria.andTaskNoEqualTo(atomicTask.getTaskNo()); // 只锁定未完成的
			 * criteria.andTaskStateNotEqualTo(AtomicTaskState.COMPLETE.name());
			 * criteria.andCurExecutorIsNull();
			 */
			// return taskDaoExt.updateByExampleSelective(toUpdate, where) == 1
			// ? toUpdate.getCurExecutor() : null;
			return taskDao.updateSelectiveByExample(toUpdate, false, false, null) == 1 ? toUpdate.getCurExecutor()
					: null;
		} else if (DateUtils.addMinutes(atomicTask.getLastExecTime(), atomicTask.getExecExpireMinute())
				.before(new Date())) {
			// 执行已超时
			AutoTask toUpdate = new AutoTask();
			toUpdate.setLastExecTime(new Date());
			toUpdate.setCurExecutor(ThreadIdentityUtils.getIdentity());
			toUpdate.setExecutedTimes(atomicTask.getExecutedTimes() + 1);
			toUpdate.setTaskState(AtomicTaskState.EXECUTING.name());
			toUpdate.setLastExecutor(toUpdate.getCurExecutor());
			toUpdate.setUpdateTime(new Date());
			toUpdate.setTaskNo(atomicTask.getTaskNo());
			toUpdate.setTaskType(atomicTask.getTaskType());
			/*
			 * AtomicTaskExample where = new AtomicTaskExample(); Criteria
			 * criteria = where.createCriteria();
			 * criteria.andTaskTypeEqualTo(atomicTask.getTaskType());
			 * criteria.andTaskNoEqualTo(atomicTask.getTaskNo()); // 只锁定未完成的
			 * criteria.andTaskStateNotEqualTo(AtomicTaskState.COMPLETE.name());
			 * criteria.andCurExecutorEqualTo(atomicTask.getCurExecutor());
			 * criteria.andLastExecTimeLessThan(DateUtils.addMinutes(new Date(),
			 * 0 - atomicTask.getExecExpireMinute()));
			 */
			// return taskDaoExt.updateByExampleSelective(toUpdate, where) == 1
			// ? toUpdate.getCurExecutor() : null;
			return taskDao.updateSelectiveByExample(toUpdate, true, true,
					DateUtils.addMinutes(new Date(), 0 - atomicTask.getExecExpireMinute())) == 1
							? toUpdate.getCurExecutor() : null;
		}
		return null;
	}

	@Override
	public void asynExecuteTask(final String taskType, final String taskNo) {
//		asynExecutorService.execute(new InheritTraceNoRunableWrapper(new Runnable() {
//			@Override
//			public void run() {
//				try {
//					syncExecuteTask(taskType, taskNo);
//				} catch (LockingTaskFailedException e) {
//					// 多台执行，忽略锁定异常
//				} catch (Exception e) {
//					logger.error(e.getMessage(), e);
//				}
//			}
//		}));

		asynExecutorService.execute(new Runnable() {
			@Override
			public void run() {
				try {
					syncExecuteTask(taskType, taskNo);
				} catch (LockingTaskFailedException e) {
					// 多台执行，忽略锁定异常
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
		});
	
	}

	@Override
	public TaskResult getAtomicTask(String taskType, String taskNo) {
		/*
		 * AtomicTaskKey atomicTaskKey = new AtomicTaskKey();
		 * atomicTaskKey.setTaskType(taskType); atomicTaskKey.setTaskNo(taskNo);
		 */
		// AtomicTask atomicTask = taskDaoExt.selectByPrimaryKey(atomicTaskKey);
		AutoTask atomicTask = taskDao.findTaskById(taskNo, taskType);
		if (atomicTask != null) {
			Map<String, String> mapStr = toMapString(atomicTask);
			TaskResult result = new TaskResult(atomicTask.getTaskState().equals(AtomicTaskState.COMPLETE.name()),
					mapStr);
			return result;
		}
		return null;
	}

	private Map<String, String> toMapString(AutoTask atomicTask) {
		Map<String, String> mapStr = new HashMap<String, String>();
		JSONObject parseObject = JSON.parseObject(atomicTask.getContextJson());
		for (Map.Entry<String, Object> entry : parseObject.entrySet()) {
			mapStr.put(entry.getKey(), entry.getValue() != null ? entry.getValue() + "" : null);
		}
		return mapStr;
	}

	@Override
	public void compensateNotCompleteTask() {
		// List<AtomicTask> queryCompensateTask =
		// taskDaoExt.queryCompensateTask();
		List<AutoTask> queryCompensateTask = taskDao.queryCompensateTask();
		for (final AutoTask atomicTask : queryCompensateTask) {
			if (!SpringCallbackUtils.isBeanAndMethodExist(applicationContext, atomicTask.getTaskMethod(),
					TaskExecResult.class, TaskExecContext.class)) {
				// 同一个数据库，多个应用公用，忽略自己不能补偿的
				continue;
			}
//			compensateExecutorService.execute(new TraceNoRunableWrapper(new Runnable() {
//				@Override
//				public void run() {
//					try {
//						syncExecuteTask(String.valueOf(atomicTask.getTaskType()), atomicTask.getTaskNo());
//					} catch (LockingTaskFailedException e) {
//						// 多台补，忽略锁定异常
//					} catch (Exception e) {
//						logger.error(e.getMessage(), e);
//					}
//				}
//			}));
			
			compensateExecutorService.execute(new Runnable() {
				@Override
				public void run() {
					try {
						syncExecuteTask(String.valueOf(atomicTask.getTaskType()), atomicTask.getTaskNo());
					} catch (LockingTaskFailedException e) {
						// 多台补，忽略锁定异常
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}
				}
			});
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		asynExecutorService = new ThreadPoolExecutor(asynThreadCount, asynThreadCount, 60L, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>(), new PrefixThreadFactory("atomic-task-asyn"));
		compensateExecutorService = new ThreadPoolExecutor(compensateThreadCount, compensateThreadCount, 60L,
				TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(),
				new PrefixThreadFactory("atomic-task-compensate"));
		/*
		 * try { StatusExtensionRegister.getInstance().register(new
		 * AtomicTaskStatusExtension(asynExecutorService,
		 * compensateExecutorService)); } catch (Throwable e) { // 忽略cat的错误 }
		 */
	}

	@Override
	public void destroy() throws Exception {
		if (asynExecutorService != null) {
			asynExecutorService.shutdown();
		}
		if (compensateExecutorService != null) {
			compensateExecutorService.shutdown();
		}
	}

}
