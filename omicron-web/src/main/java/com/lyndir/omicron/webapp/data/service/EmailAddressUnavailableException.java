package com.lyndir.omicron.webapp.data.service;

public class EmailAddressUnavailableException extends ModelException {

    public EmailAddressUnavailableException(final String address) {
        super( String.format( "Address unavailable: `%s`", address ) );
    }
}
