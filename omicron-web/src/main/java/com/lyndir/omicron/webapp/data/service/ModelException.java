package com.lyndir.omicron.webapp.data.service;

/**
 * @author lhunath, 2013-10-22
 */
public abstract class ModelException extends Exception {

    protected ModelException(final String message) {
        super( message );
    }

    protected ModelException(final String message, final Throwable cause) {
        super( message, cause );
    }

    protected ModelException(final Throwable cause) {
        super( cause );
    }
}
