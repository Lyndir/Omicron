package com.lyndir.omicron.api.error;

/**
 * @author lhunath, 2013-08-18
 */
public class OmicronSecurityException extends RuntimeException {

    protected OmicronSecurityException(final String format, final Object... args) {
        super( String.format( format, args ) );
    }
}
