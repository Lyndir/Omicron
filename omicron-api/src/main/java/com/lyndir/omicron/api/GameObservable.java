package com.lyndir.omicron.api;

import com.lyndir.omicron.api.util.Maybe;
import java.util.Optional;


/**
 * Any object that can be observed in the game.
 *
 * @author lhunath, 2/25/2014
 */
public interface GameObservable {

    /**
     * @return The player that has control over this observable, if any.
     */
    Optional<? extends IPlayer> getOwner();

    /**
     * @return The observable's location or {@link Maybe#unknown()} if the current player doesn't own the observable and none of its game objects can observe it.
     */
    Maybe<? extends ITile> getLocation();
}
