package com.lyndir.omicron.webapp.data.service;

import com.lyndir.omicron.webapp.data.EmailAddress;


/**
 * @author lhunath
 */
public interface EmailAddressDAO {

    EmailAddress newAddress(String address)
            throws EmailAddressUnavailableException;
}
