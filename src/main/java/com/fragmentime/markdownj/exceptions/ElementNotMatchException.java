package com.fragmentime.markdownj.exceptions;

/**
 * Created by Beancan on 2016/10/26.
 */
public class ElementNotMatchException extends Exception {
    public ElementNotMatchException() {
        super();
    }

    public ElementNotMatchException(String message) {
        super(message);
    }

    public ElementNotMatchException(String message, Throwable cause) {
        super(message, cause);
    }

    public ElementNotMatchException(Throwable cause) {
        super(cause);
    }

    protected ElementNotMatchException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
