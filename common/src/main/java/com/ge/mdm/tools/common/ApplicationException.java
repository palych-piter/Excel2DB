package com.ge.mdm.tools.common;

/**
 * @author Stanislav Mamontov
 */
public class ApplicationException extends Exception {
    static final long serialVersionUID = 1L;

    public ApplicationException() {
        super();
    }

    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApplicationException(Throwable cause) {
        super(cause);
    }
}