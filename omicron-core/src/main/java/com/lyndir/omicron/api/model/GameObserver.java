package com.lyndir.omicron.api.model;

import com.google.common.base.Optional;
import com.lyndir.omicron.api.Authenticated;
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
     *
     * @param location      The tile that this observer is trying to see.
     *
     * @return true if the current player is allowed to know and the given tile is visible to this observer.
     */
    @Authenticated
    boolean canObserve(@Nonnull Tile location);

    /**
     * Enumerate the tiles this observer can observe.
     *
     * @return All the tiles observable both by this observer and the current player.
     */
    @Nonnull
    @Authenticated
    Iterable<Tile> listObservableTiles();

    @Nonnull
    Optional<Player> getOwner();
}
