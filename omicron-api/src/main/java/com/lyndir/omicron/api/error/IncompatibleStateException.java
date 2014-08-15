package com.lyndir.omicron.api.error;

/**
 * @author lhunath, 2013-08-17
 */
public abstract class IncompatibleStateException extends Exception {

    protected IncompatibleStateException(final String format, final Object... args) {
        super( String.format( format, args ) );
    }
}
