package com.lyndir.omicron.webapp.data.service.impl.jpa;

import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.lyndir.lhunath.opal.jpa.Persist;
import com.lyndir.omicron.webapp.data.EmailAddress;
import com.lyndir.omicron.webapp.data.service.EmailAddressDAO;
import com.lyndir.omicron.webapp.data.service.EmailAddressUnavailableException;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;


/**
 * @author lhunath
 */
public class EmailAddressDAOImpl implements EmailAddressDAO {

    private final EntityManager   db;

    @Inject
    public EmailAddressDAOImpl(final Persist persist) {
        db = persist.getEntityManager();
    }

    @Override
    public EmailAddress newAddress(final String address)
            throws EmailAddressUnavailableException {
        EmailAddress emailAddress = Iterables.getFirst(
                db.createQuery( "SELECT e FROM LLEmailAddress e WHERE e.address = :address", EmailAddress.class ) //
                        .setParameter( "address", address ) //
                        .getResultList(), null );
        if (emailAddress != null)
            throw new EmailAddressUnavailableException( address );

        emailAddress = new EmailAddress( address );
        db.persist( emailAddress );

        return emailAddress;
    }
}
