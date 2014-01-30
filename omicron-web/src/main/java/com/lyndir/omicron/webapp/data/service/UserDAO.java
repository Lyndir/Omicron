package com.lyndir.omicron.webapp.data.service;

import com.lyndir.omicron.webapp.data.*;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * @author lhunath
 */
public interface UserDAO {

    @Nonnull
    User newUser(String emailAddress, String name)
            throws EmailAddressUnavailableException;

    @Nullable
    User findUser(String emailAddress);

    @Nonnull
    List<User> listUsers();
}
