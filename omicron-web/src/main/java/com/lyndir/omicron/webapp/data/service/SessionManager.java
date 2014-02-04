package com.lyndir.omicron.webapp.data.service;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.lyndir.omicron.webapp.data.User;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpSession;


/**
 * @author lhunath, 1/28/2014
 */
public class SessionManager {

    private static final String USER         = "USER";

    private final Provider<HttpSession> httpSessionProvider;

    @Inject
    public SessionManager(final Provider<HttpSession> httpSessionProvider) {
        this.httpSessionProvider = httpSessionProvider;
    }

    @Nonnull
    public User getUser() {
        return Preconditions.checkNotNull( findUser(), "No user.  Please first authenticate." );
    }

    @Nullable
    public User findUser() {
        return (User) httpSessionProvider.get().getAttribute( USER );
    }

    public void setUser(final User user) {
        httpSessionProvider.get().setAttribute( USER, user );
    }
}
