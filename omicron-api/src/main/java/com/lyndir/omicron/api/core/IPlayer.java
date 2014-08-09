package com.lyndir.omicron.api.core;

import com.google.common.collect.ImmutableMap;
import com.lyndir.omicron.api.util.Maybool;
import javax.annotation.Nonnull;
import org.jetbrains.annotations.NotNull;


/**
 * <i>10 15, 2012</i>
 *
 * @author lhunath
 */
public interface IPlayer extends GameObserver {

    /**
     * @return The unique identifier of the player in the game.
     */
    long getPlayerID();

    /**
     * @return The visible name of this player.
     */
    String getName();

    /**
     * @return The primary color for this player's units.
     */
    Color getPrimaryColor();

    /**
     * @return The secondary color for this player's units.
     */
    Color getSecondaryColor();

    /**
     * @return This player's current score.
     */
    int getScore();

    /**
     * @return The live objects this player currently controls mapped by object identifier.
     */
    ImmutableMap<Long, IGameObject> getObjectsByID();

    /**
     * @return Determines whether this is the currently authenticated player.
     */
    boolean isCurrentPlayer();

    @Nonnull
    IPlayerController getController();

    @Override
    default Maybool canObserve(@Nonnull final GameObservable observable) {
        return getController().canObserve( observable );
    }

    @NotNull
    @Override
    default Iterable<? extends ITile> iterateObservableTiles() {
        return getController().iterateObservableTiles();
    }
}
