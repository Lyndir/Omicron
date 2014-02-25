package com.lyndir.omicron.api.model;

import com.lyndir.omicron.api.util.Maybe;


/**
 * @author lhunath, 2/25/2014
 */
public interface GameObservable {

    /**
     * @return The player that has control over this observable, if any.
     */
    Maybe<? extends IPlayer> checkOwner();

    /**
     * @return The observable's location or {@link Maybe#unknown()} if the current player doesn't own the observable and none of its game objects can observe it.
     */
    Maybe<? extends ITile> checkLocation()
            throws Security.NotAuthenticatedException;
}
