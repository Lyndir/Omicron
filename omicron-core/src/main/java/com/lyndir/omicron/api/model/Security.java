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

import static com.lyndir.omicron.api.model.IncompatibleStateException.*;

import com.google.common.base.Preconditions;
import com.lyndir.lhunath.opal.system.util.ObjectUtils;


/**
 * @author lhunath, 2013-08-10
 */
public final class Security {

    private static final ThreadLocal<Player> currentPlayerTL = new ThreadLocal<>();

    public static void authenticate(final Player currentPlayer, final PlayerKey playerKey) {
        Preconditions.checkArgument( currentPlayer.hasKey( playerKey ), "Cannot authenticate, key does not match player: ", currentPlayer );

        currentPlayerTL.set( currentPlayer );
    }

    static boolean isAuthenticated() {
        return currentPlayerTL.get() != null;
    }

    static Player currentPlayer()
            throws IncompatibleStateException {
        return assertNotNull( currentPlayerTL.get(), NotAuthenticatedException.class );
    }

    public static void assertOwned(final GameObserver observer)
            throws IncompatibleStateException {
        assertState( observer.getOwner().isPresent() && ObjectUtils.equals( observer.getOwner().get(), currentPlayer() ), //
                     NotOwnedException.class, observer );
    }

    public static void assertObservable(final Tile location) {
        assertState( currentPlayer().canObserve( location ).isTrue(), //
                     NotObservableException.class, location );
    }

    public static class NotAuthenticatedException extends IncompatibleStateException {

        NotAuthenticatedException() {
            super( "Not authenticated.  To perform this action, first authenticate using Security#authenticate." );
        }
    }


    public static class NotOwnedException extends IncompatibleStateException {

        NotOwnedException(final GameObserver observer) {
            super( "Not owned by current player: %s", observer );
        }
    }


    public static class NotObservableException extends IncompatibleStateException {

        NotObservableException(final Tile location) {
            super( "Not observable by current player: %s", location );
        }
    }
}
