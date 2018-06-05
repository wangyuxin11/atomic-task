package com.wanda.base.task.exception;

/**
 * description: 
 * @author senvon
 * time : 2015年4月17日 上午10:29:14
 */
public class DuplicateTaskException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public DuplicateTaskException() {
    }

    /**
     * @param message
     */
    public DuplicateTaskException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public DuplicateTaskException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public DuplicateTaskException(String message, Throwable cause) {
        super(message, cause);
    }

}
