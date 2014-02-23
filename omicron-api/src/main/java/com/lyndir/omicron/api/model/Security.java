/*
 * Copyright 2010, Maarten Billemont
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.lyndir.omicron.api.model;

import static com.lyndir.omicron.api.model.error.ExceptionUtils.*;

import com.google.common.base.Preconditions;
import com.lyndir.lhunath.opal.system.util.Job;
import com.lyndir.lhunath.opal.system.util.ObjectUtils;
import com.lyndir.omicron.api.model.error.OmicronSecurityException;
import com.lyndir.omicron.api.util.Maybe;
import java.util.Deque;
import java.util.LinkedList;
import javax.annotation.Nonnull;


/**
 * @author lhunath, 2013-08-10
 */
public final class Security {

    private static final ThreadLocal<IPlayer>        currentPlayerTL = new ThreadLocal<>();
    private static final ThreadLocal<Deque<IPlayer>> jobPlayerTL     = new ThreadLocal<Deque<IPlayer>>() {
        @Override
        protected Deque<IPlayer> initialValue() {
            return new LinkedList<>();
        }
    };
    private static final ThreadLocal<Deque<Boolean>> godTL           = new ThreadLocal<Deque<Boolean>>() {
        @Override
        protected Deque<Boolean> initialValue() {
            return new LinkedList<>();
        }
    };

    static <R> R godRun(final Job<R> job) {
        if (isGod())
            // Already god.
            return job.execute();

        try {
            // Become god.
            godTL.get().push( true );
            return job.execute();
        }
        finally {
            // Become mortal.
            Preconditions.checkState( godTL.get().pop(), "Expected to be god." );
        }
    }

    static void playerRun(final IPlayer jobPlayer, final Runnable job) {
        try {
            godTL.get().push( false );
            jobPlayerTL.get().push( jobPlayer );
            job.run();
        }
        finally {
            Preconditions.checkState( !godTL.get().pop(), "Expected to not be god." );
            Preconditions.checkState( jobPlayerTL.get().pop() == jobPlayer, "Expected to pop player for job." );
        }
    }

    public static void authenticate(final IPlayer currentPlayer, final PlayerKey playerKey) {
        Preconditions.checkArgument( currentPlayer.hasKey( playerKey ), "Cannot authenticate, key does not match player: ", currentPlayer );

        currentPlayerTL.set( currentPlayer );
    }

    public static void authenticatedRun(final IPlayer currentPlayer, final PlayerKey playerKey, final Runnable job) {
        Preconditions.checkArgument( currentPlayer.hasKey( playerKey ), "Cannot authenticate, key does not match player: ", currentPlayer );

        playerRun( currentPlayer, job );
    }

    static boolean isAuthenticated() {
        return currentPlayerTL.get() != null;
    }

    static boolean isGod() {
        return !godTL.get().isEmpty() && godTL.get().peek();
    }

    @Nonnull
    static IPlayer currentPlayer()
            throws NotAuthenticatedException {
        IPlayer jobPlayer = jobPlayerTL.get().peek();
        if (jobPlayer != null)
            return jobPlayer;

        IPlayer currentPlayer = currentPlayerTL.get();
        assertSecure( currentPlayer != null, NotAuthenticatedException.class );
        assert currentPlayer != null;

        return currentPlayer;
    }

    public static void assertOwned(final GameObserver observer)
            throws NotAuthenticatedException, NotOwnedException {
        if (isGod())
            return;

        assertSecure( observer.getOwner().isPresent() && ObjectUtils.equals( observer.getOwner().get(), currentPlayer() ), //
                      NotOwnedException.class, observer );
    }

    public static void assertObservable(final ITile location)
            throws NotAuthenticatedException, NotObservableException {
        if (isGod())
            return;

        assertSecure( currentPlayer().canObserve( location ).isTrue(), //
                      NotObservableException.class, location );
    }

    public static void assertObservable(final IGameObject gameObject)
            throws NotAuthenticatedException, NotObservableException {
        if (isGod())
            return;

        assertSecure( gameObject.checkLocation().presence() == Maybe.Presence.PRESENT, //
                      NotObservableException.class, gameObject );
    }

    public static class NotAuthenticatedException extends OmicronSecurityException {

        NotAuthenticatedException() {
            super( "Not authenticated.  To perform this action, first authenticate using Security#authenticate." );
        }
    }


    public static class NotOwnedException extends OmicronSecurityException {

        NotOwnedException(final GameObserver observer) {
            super( "Not owned by current player: %s", observer );
        }
    }


    public static class NotObservableException extends OmicronSecurityException {

        NotObservableException(final ITile location) {
            super( "Not observable by current player: %s", location );
        }

        NotObservableException(final IGameObject gameObject) {
            super( "Not observable by current player: %s", gameObject );
        }
    }
}
