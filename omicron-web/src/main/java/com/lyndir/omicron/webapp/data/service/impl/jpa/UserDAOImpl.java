package com.lyndir.omicron.webapp.data.service.impl.jpa;

import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.lyndir.lhunath.opal.jpa.Persist;
import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.omicron.webapp.data.*;
import com.lyndir.omicron.webapp.data.service.*;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.EntityManager;


/**
 * @author lhunath
 */
public class UserDAOImpl implements UserDAO {

    private static final Logger logger = Logger.get( UserDAOImpl.class );

    private final EntityManager   db;
    private final EmailAddressDAO emailAddressDAO;

    @Inject
    public UserDAOImpl(final Persist persist, final EmailAddressDAO emailAddressDAO) {
        db = persist.getEntityManager();
        this.emailAddressDAO = emailAddressDAO;
    }

    @Nonnull
    @Override
    public User newUser(final String emailAddress, final String name)
            throws EmailAddressUnavailableException {
        User user = new User( emailAddressDAO.newAddress( emailAddress ), name );
        db.persist( user );

        return user;
    }

    @Override
    @Nullable
    public User findUser(final String emailAddress) {
        return Iterables.getFirst( db.createQuery( "SELECT u FROM LLEmailAddress e JOIN e.user u " + //
                                                   "WHERE u.mode = :mode AND e.address = :emailAddress", User.class )
                                     .setParameter( "emailAddress", emailAddress )
                                     .getResultList(), null );
    }

    @Nonnull
    @Override
    public List<User> listUsers() {
        return db.createQuery( "SELECT u FROM LLUser u", User.class ).getResultList();
    }
}
