package com.wanda.base.task.vo;

import java.util.Map;

/**
 * description: 
 * @author senvon
 * time : 2015年4月17日 上午10:35:57
 */
public class SyncTaskResult extends TaskResult {
    private Object syncResult;

    public SyncTaskResult(boolean done, Map<String, String> context, Object syncResult) {
        super(done, context);
        this.syncResult = syncResult;
    }

    public Object getSyncResult() {
        return syncResult;
    }

    public void setSyncResult(Object syncResult) {
        this.syncResult = syncResult;
    }
}
