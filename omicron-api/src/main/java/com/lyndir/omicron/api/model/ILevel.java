package com.lyndir.omicron.api.model;

import com.google.common.base.Optional;
import java.util.Map;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
public interface ILevel {

    Size getSize();

    LevelType getType();

    IGame getGame();

    Map<Coordinate, ? extends ITile> getTiles();

    /**
     * Get the tile at the given position in this level.
     *
     *
     *
     * @param position The position of the tile to get.
     *
     * @return {@code null} if the position is outside of the bounds of this level.
     */
    Optional<? extends ITile> getTile(final Coordinate position);

    /**
     * Get the tile at the given position in this level.
     *
     *
     * @param u The u coordinate of the position of the tile to get.
     * @param v The v coordinate of the position of the tile to get.
     *
     * @return {@code null} if the position is outside of the bounds of this level.
     */
    Optional<? extends ITile> getTile(final int u, final int v);
}
