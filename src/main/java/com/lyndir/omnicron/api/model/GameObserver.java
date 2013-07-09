package com.lyndir.omnicron.api.model;

import com.google.common.collect.FluentIterable;
import com.lyndir.omnicron.api.controller.GameObjectController;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * <i>10 16, 2012</i>
 *
 * @author lhunath
 */
public interface GameObserver {

    /**
     * Check whether the current object can observe the tile at the given location.
     *
     * @param currentPlayer The player that's making the request.
     * @param location      The tile that this observer is trying to see.
     *
     * @return true if the current player is allowed to know and the given tile is visible to this observer.
     */
    boolean canObserve(@NotNull Player currentPlayer, @NotNull Tile location);

    /**
     * Enumerate the tiles this observer can observe.
     *
     *
     *
     * @param currentPlayer The player that's making the request.
     *
     * @return All the tiles observable both by this observer and the current player.
     */
    @NotNull
    Iterable<Tile> listObservableTiles(@NotNull Player currentPlayer);

    @Nullable
    Player getPlayer();
}
