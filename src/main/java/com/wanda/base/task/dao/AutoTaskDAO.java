package com.wanda.base.task.dao;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.wanda.base.task.enums.AtomicTaskState;
import com.wanda.base.task.model.AutoTask;

public class AutoTaskDAO {

	private DataSource datasource;
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private static final Map<String , String> fieldToColumnMap = new HashMap<String , String>();
//	private static final Map<String , String> columnToFieldMap = new HashMap<String , String>();
	
	static{
		fieldToColumnMap.put("taskNo", "TASK_NO");
//		columnToFieldMap.put("TASK_NO", "taskNo");
		
		fieldToColumnMap.put("taskType", "TASK_TYPE");
//		columnToFieldMap.put("TASK_TYPE", "taskType");
		
		fieldToColumnMap.put("taskMethod", "TASK_METHOD");
//		columnToFieldMap.put("TASK_METHOD", "taskMethod");
		
		fieldToColumnMap.put("nextExecTime", "NEXT_EXEC_TIME");
//		columnToFieldMap.put("NEXT_EXEC_TIME", "nextExecTime");
		
		fieldToColumnMap.put("curExecutor", "CUR_EXECUTOR");
//		columnToFieldMap.put("CUR_EXECUTOR", "curExecutor");
		
		fieldToColumnMap.put("lastExecTime", "LAST_EXEC_TIME");
//		columnToFieldMap.put("LAST_EXEC_TIME", "lastExecTime");
		
		fieldToColumnMap.put("lastExecedTime", "LAST_EXECED_TIME");
//		columnToFieldMap.put("LAST_EXECED_TIME", "lastExecedTime");
		
		fieldToColumnMap.put("lastExecutor", "LAST_EXECUTOR");
//		columnToFieldMap.put("LAST_EXECUTOR", "lastExecutor");
		
		fieldToColumnMap.put("execExpireMinute", "EXEC_EXPIRE_MINUTE");
//		columnToFieldMap.put("EXEC_EXPIRE_MINUTE", "execExpireMinute");
		
		fieldToColumnMap.put("executedTimes", "EXECUTED_TIMES");
//		columnToFieldMap.put("EXECUTED_TIMES", "executedTimes");
		
		fieldToColumnMap.put("maxExecTimes", "MAX_EXEC_TIMES");
//		columnToFieldMap.put("MAX_EXEC_TIMES", "maxExecTimes");
		
		fieldToColumnMap.put("contextJson", "CONTEXT_JSON");
//		columnToFieldMap.put("CONTEXT_JSON", "contextJson");
		
		fieldToColumnMap.put("execMsg", "EXEC_MSG");
//		columnToFieldMap.put("EXEC_MSG", "execMsg");
		
		fieldToColumnMap.put("taskState", "TASK_STATE");
//		columnToFieldMap.put("TASK_STATE", "taskState");
		
		fieldToColumnMap.put("createTime", "CREATE_TIME");
//		columnToFieldMap.put("CREATE_TIME", "createTime");
		
		fieldToColumnMap.put("updateTime", "UPDATE_TIME");
//		columnToFieldMap.put("UPDATE_TIME", "updateTime");
	}
	
	public void setDatasource(DataSource datasource) {
		this.datasource = datasource;
	}

	private Map<String , Object> buildParamMap(AutoTask task){
		Field[] fieldArray = FieldUtils.getAllFields(AutoTask.class);
		if(fieldArray != null){
			Map<String ,Object> paramMap = new HashMap<String , Object>();
			for(Field targetField : fieldArray){
				try {
					Object value = FieldUtils.readField(targetField, task, true);
					if(value != null){
						paramMap.put(targetField.getName(), value);
					}
				} catch (IllegalAccessException e) {
					logger.error("can't read field value for field:"+targetField.getName() , e);
				}
			}
			return paramMap;
		}
		return null;
	}
	
	private String buildSelectiveUpdateSqlByKey(AutoTask task){
		StringBuffer sb = new StringBuffer();
		
		Field[] fieldArray = FieldUtils.getAllFields(AutoTask.class);
		if(fieldArray != null){
			for(Field targetField : fieldArray){
				if(targetField.getName().equals("serialVersionUID")){
					continue;
				}
				String columnName = fieldToColumnMap.get(targetField.getName());
				Object fieldValue = null;
				try {
					fieldValue = FieldUtils.readField(targetField, task, true);
				} catch (Exception e) {
				}
				if(StringUtils.isBlank(columnName) ||  fieldValue == null){
					continue;
				}
				if(sb.length()>0){
					sb.append(",");
				}
				sb.append(columnName).append("=").append(":").append(targetField.getName());
			}
		}
		
		sb.insert(0 , "UPDATE AUTO_TASK SET ");
		sb.append(" WHERE TASK_NO=:taskNo AND TASK_TYPE=:taskType");
		logger.info("=============the update sql:{}" , sb.toString());
		return sb.toString(); 
	}
	
	private String buildSelectiveUpdateSql(AutoTask task , boolean needCurExecutor , boolean needLastExpire){
		StringBuffer sb = new StringBuffer();
		Field[] fieldArray = FieldUtils.getAllFields(AutoTask.class);
		if(fieldArray != null){
			for(Field targetField : fieldArray){
				if(targetField.getName().equals("serialVersionUID")){
					continue;
				}
				String columnName = fieldToColumnMap.get(targetField.getName());
				Object fieldValue = null;
				try {
					fieldValue = FieldUtils.readField(targetField, task, true);
				} catch (Exception e) {
				}
				if(StringUtils.isBlank(columnName) ||  fieldValue == null){
					continue;
				}
				if(sb.length()>0){
					sb.append(",");
				}
				sb.append(columnName).append("=").append(":").append(targetField.getName());
			}
		}
		
		sb.insert(0 , "UPDATE AUTO_TASK SET ");
		sb.append(" WHERE TASK_NO=:taskNo AND TASK_TYPE=:taskType AND TASK_STATE <> :notTaskState");
		if(needCurExecutor){
			sb.append(" AND CUR_EXECUTOR = :curExecutor");
		}else{
			sb.append(" AND CUR_EXECUTOR IS NULL ");
		}
		
		if(needLastExpire){
			sb.append(" AND LAST_EXECED_TIME< :moreLastExecedTime");
		}
		logger.info("=======update by selective:{}=====" , sb.toString());
		return sb.toString();
	}
	
	private class AutoTaskRowMapper implements RowMapper<AutoTask>{
		@Override
		public AutoTask mapRow(ResultSet rs, int rowNum) throws SQLException {
			AutoTask result = new AutoTask();
			/*result.setTaskNo(rs.getString("TASK_NO"));
			result.setTaskType(rs.getString("TASK_TYPE"));
			result.setTaskMethod(rs.getString("TASK_METHOD"));
			result.setNextExecTime(rs.getTimestamp("NEXT_EXEC_TIME"));
			result.setCurExecutor(rs.getString("CUR_EXECUTOR"));
			result.setLastExecTime(rs.getTimestamp("LAST_EXEC_TIME"));
			result.setLastExecedTime(rs.getTimestamp("LAST_EXECED_TIME"));
			result.setLastExecutor(rs.getString("LAST_EXECUTOR"));
			result.setExecExpireMinute(rs.getInt("EXEC_EXPIRE_MINUTE"));
			result.setExecutedTimes(rs.getInt("EXECUTED_TIMES"));
			result.setMaxExecTimes(rs.getInt("MAX_EXEC_TIMES"));
			result.setContextJson(rs.getString("CONTEXT_JSON"));
			result.setExecMsg(rs.getString("EXEC_MSG"));
			result.setTaskState(rs.getString("TASK_STATE"));
			result.setCreateTime(rs.getTimestamp("CREATE_TIME"));
			result.setUpdateTime(rs.getTimestamp("UPDATE_TIME"));*/
			
			Field[] fieldArray = FieldUtils.getAllFields(AutoTask.class);
			if(fieldArray != null){
				for(Field targetField : fieldArray){
					if(targetField.getName().equals("serialVersionUID")){
						continue;
					}
					String columnName = fieldToColumnMap.get(targetField.getName());
					if(StringUtils.isBlank(columnName)){
						continue;
					}
					try {
						Object value = findValue(rs , columnName , targetField.getType());
						FieldUtils.writeField(targetField, result, value,true);
					} catch (Exception e) {
						logger.error("can't write field value for field:"+targetField.getName() , e);
					}
				}
			}
			
			return result;
		}
		
		public Object findValue(ResultSet rs , String columnName , Class<?> clazz) throws SQLException{
			if(CharSequence.class.isAssignableFrom(clazz)){
				return rs.getString(columnName);
			}else if(Date.class.isAssignableFrom(clazz)){
				return rs.getTimestamp(columnName);
			}else if(clazz.equals(Integer.class) || clazz.equals(int.class)){
				return rs.getInt(columnName);
			}else if(clazz.equals(Long.class) || clazz.equals(long.class)){
				return rs.getLong(columnName);
			}else if(Number.class.isAssignableFrom(clazz)){
				return rs.getBigDecimal(columnName);
			}
			return null;
		}
	}
	
	public int insertTask(AutoTask task){
		NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(datasource);
		
		String insertSql = "INSERT INTO AUTO_TASK(TASK_NO,TASK_TYPE,TASK_METHOD,NEXT_EXEC_TIME,EXEC_EXPIRE_MINUTE,MAX_EXEC_TIMES,EXECUTED_TIMES,CONTEXT_JSON,TASK_STATE,CREATE_TIME,UPDATE_TIME) "
				+ "values(:taskNo , :taskType , :taskMethod , :nextExecTime , :execExpireMinute , :maxExecTimes ,:executedTimes,:contextJson , :taskState , :createTime , :updateTime)";
		Map<String ,Object> paramMap = buildParamMap(task);
		logger.info("==========insert paramMap :{}" , paramMap);
		return template.update(insertSql, paramMap);
	}
	
	public AutoTask findTaskById(String taskNo , String taskType){
		NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(datasource);
		String querySql = "SELECT * FROM AUTO_TASK WHERE TASK_NO=:taskNo AND TASK_TYPE=:taskType";
		Map<String , Object> paramMap = new HashMap<String , Object>();
		paramMap.put("taskNo", taskNo);
		paramMap.put("taskType", taskType);
		return template.queryForObject(querySql, paramMap, new AutoTaskRowMapper());
	}
	
	
	public int updateSelectiveByKey(AutoTask task){
		NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(datasource);
		String updateSql = buildSelectiveUpdateSqlByKey(task);
		Map<String ,Object> paramMap = buildParamMap(task);
		logger.info("==========updateBykey paramMap :{}" , paramMap);
		return template.update(updateSql, paramMap);
	}
	
	public int updateSelectiveByExample(AutoTask task , boolean needCurExecutor , boolean needLastExpire , Date moreLastExecedTime){
		NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(datasource);
		String updateSql = buildSelectiveUpdateSql(task , needCurExecutor , needLastExpire);
		Map<String , Object> paramMap = buildParamMap(task);
		paramMap.put("notTaskState", AtomicTaskState.COMPLETE.name());
		paramMap.put("moreLastExecedTime", moreLastExecedTime);
		logger.info("==========updateByExample paramMap :{}" , paramMap);
		return template.update(updateSql, paramMap);
	}
	
	public int unlockTask(String taskNo , String taskType , String curExecutor){
		NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(datasource);
		String updateSql = "UPDATE AUTO_TASK SET CUR_EXECUTOR = null, UPDATE_TIME = :now WHERE TASK_TYPE = :taskType and TASK_NO = :taskNo and CUR_EXECUTOR = :curExecutor";
		Map<String , Object> paramMap = new HashMap<String , Object>();
		paramMap.put("taskNo", taskNo);
		paramMap.put("taskType", taskType);
		paramMap.put("curExecutor", curExecutor);
		paramMap.put("now", new Date());
		return template.update(updateSql, paramMap);
	}
	
	public List<AutoTask> queryCompensateTask(){
		NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(datasource);
		String querySql = "SELECT * FROM AUTO_TASK WHERE NEXT_EXEC_TIME <= :now and UPDATE_TIME > :tenDays and TASK_STATE in ('WAITING_EXECUTE','EXECUTING') AND EXECUTED_TIMES < MAX_EXEC_TIMES ORDER BY EXECUTED_TIMES";
		Map<String , Object> paramMap = new HashMap<String , Object>();
		paramMap.put("now", new Date());
		paramMap.put("tenDays", DateUtils.addDays(new Date(), -10));
		return template.query(querySql, paramMap, new AutoTaskRowMapper());
	}
	
}
