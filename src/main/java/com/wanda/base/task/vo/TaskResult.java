package com.wanda.base.task.vo;

import java.util.HashMap;
import java.util.Map;

/**
 * description: 
 * @author senvon
 * time : 2015年4月17日 上午10:34:12
 */
public class TaskResult {
    private boolean complete;
    private Map<String, String> context;

    public TaskResult(boolean complete, Map<String, String> context) {
        this.complete = complete;
        if (context != null) {
            this.context = context;
        } else {
            context = new HashMap<String, String>();
        }
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean done) {
        this.complete = done;
    }

    public Map<String, String> getContext() {
        return context;
    }

    public void setContext(Map<String, String> context) {
        this.context = context;
    }
}
