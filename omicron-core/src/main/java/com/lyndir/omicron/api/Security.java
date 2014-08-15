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


package com.lyndir.omicron.api;

import static com.google.common.base.Preconditions.checkArgument;
import static com.lyndir.omicron.api.error.ExceptionUtils.*;

import com.google.common.base.Preconditions;
import com.lyndir.lhunath.opal.system.util.Job;
import com.lyndir.lhunath.opal.system.util.ObjectUtils;
import com.lyndir.omicron.api.error.*;
import java.util.*;
import javax.annotation.Nonnull;


/**
 * @author lhunath, 2013-08-10
 */
public final class Security {

    private static final ThreadLocal<Game>          activeGameTL      = new ThreadLocal<>();
    private static final ThreadLocal<Player>        activePlayerTL    = new ThreadLocal<>();
    private static final ThreadLocal<Deque<Player>> activePlayerJobTL = new ThreadLocal<Deque<Player>>() {
        @Override
        protected Deque<Player> initialValue() {
            return new LinkedList<>();
        }
    };
    private static final ThreadLocal<Deque<Boolean>> godJobTL          = new ThreadLocal<Deque<Boolean>>() {
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
            godJobTL.get().push( true );
            return job.execute();
        }
        finally {
            // Become mortal.
            Preconditions.checkState( godJobTL.get().pop(), "Expected to be god." );
        }
    }

    static void godRun(final Runnable job) {
        if (isGod()) {
            // Already god.
            job.run();
            return;
        }

        try {
            // Become god.
            godJobTL.get().push( true );
            job.run();
        }
        finally {
            // Become mortal.
            Preconditions.checkState( godJobTL.get().pop(), "Expected to be god." );
        }
    }

    @SuppressWarnings("ObjectEquality")
    static void playerRun(final Player jobPlayer, final Runnable job) {
        try {
            godJobTL.get().push( false );
            activePlayerJobTL.get().push( jobPlayer );
            job.run();
        }
        finally {
            Preconditions.checkState( !godJobTL.get().pop(), "Expected to not be god." );
            Preconditions.checkState( activePlayerJobTL.get().pop() == jobPlayer, "Expected to pop player for job." );
        }
    }
    public static void activateGame(final Game game) {
        activeGameTL.set( game );
    }

    public static void activatePlayer(final Player player) {
        activePlayerTL.set( player );
    }

    public static void activatePlayerRun(final Player currentPlayer, final Runnable job) {
        checkArgument( currentPlayer.isCurrentPlayer(), "Cannot authenticate, player is not the current player: ", currentPlayer );

        playerRun( currentPlayer, job );
    }

    static boolean isAuthenticated() {
        return activePlayerTL.get() != null;
    }

    static boolean isGod() {
        return !godJobTL.get().isEmpty() && godJobTL.get().peek();
    }

    /**
     * @return true if the current player is god or can observe the target.
     *
     * @see Player#canObserve(GameObservable)
     */
    static boolean currentPlayerCanObserve(final GameObservable gameObservable) {
        return isGod() || currentPlayer().canObserve( gameObservable ).isTrue();
    }

    @Nonnull
    static Player currentPlayer()
            throws NotAuthenticatedException {
        Player jobPlayer = activePlayerJobTL.get().peek();
        if (jobPlayer != null)
            return jobPlayer;

        Player currentPlayer = activePlayerTL.get();
        ExceptionUtils.assertSecure( currentPlayer != null, NotAuthenticatedException.class );
        assert currentPlayer != null;

        return currentPlayer;
    }

    @Nonnull
    static Game currentGame()
            throws NotAuthenticatedException {
        Game currentGame = activeGameTL.get();
        ExceptionUtils.assertSecure( currentGame != null, NotAuthenticatedException.class );
        assert currentGame != null;

        return currentGame;
    }

    public static void assertOwned(final GameObservable observable)
            throws NotAuthenticatedException, NotOwnedException {
        if (isGod())
            return;

        Optional<? extends IPlayer> owner = observable.getOwner();
        assertSecure( owner.isPresent() && ObjectUtils.equals( owner.get(), currentPlayer() ), //
                      NotOwnedException.class, observable );
    }

    public static void assertObservable(final GameObservable observable)
            throws NotAuthenticatedException, NotObservableException {
        if (isGod())
            return;

        assertSecure( currentPlayer().canObserve( observable ).isTrue(), //
                      NotObservableException.class, observable );
    }
}
