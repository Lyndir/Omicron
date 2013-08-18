package com.lyndir.omicron.api.model;

import com.google.common.base.Optional;
import com.lyndir.omicron.api.Authenticated;
import com.lyndir.omicron.api.util.Maybe;
import com.lyndir.omicron.api.util.Maybool;
import javax.annotation.Nonnull;


/**
 * <i>10 16, 2012</i>
 *
 * @author lhunath
 */
public interface GameObserver {

    /**
     * Check whether the current object can observe the tile at the given location.
     *
     * @param location The tile that this observer is trying to see.
     *
     * @return true if the current player is allowed to know and the given tile is visible to this observer.
     */
    @Authenticated
    Maybool canObserve(@Nonnull Tile location);

    /**
     * Enumerate the tiles this observer can observe.
     *
     * @return All the tiles observable both by this observer and the current player.
     */
    @Nonnull
    @Authenticated
    Iterable<Tile> listObservableTiles();

    /**
     * @return The player that has control over this observer, if any.
     */
    @Nonnull
    // TODO: This should return a Maybe to avoid being able to detect ownership changes on objects that are not observable.
    Optional<Player> getOwner();
}
