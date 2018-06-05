package com.wanda.base.task.exception;

/**
 * description: 
 * @author senvon
 * time : 2015年4月17日 上午10:31:35
 */
public class LockingTaskFailedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public LockingTaskFailedException() {
    }

    /**
     * @param message
     */
    public LockingTaskFailedException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public LockingTaskFailedException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public LockingTaskFailedException(String message, Throwable cause) {
        super(message, cause);
    }

}
