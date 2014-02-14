package com.lyndir.omicron.api.model;

import com.google.common.base.Optional;
import com.lyndir.lhunath.opal.math.Size;
import com.lyndir.lhunath.opal.math.Vec2Hex;
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

    Map<Vec2Hex, ? extends ITile> getTiles();

    /**
     * Get the tile at the given position in this level.
     *
     *
     *
     * @param position The position of the tile to get.
     *
     * @return {@code null} if the position is outside of the bounds of this level.
     */
    Optional<? extends ITile> getTile(Vec2Hex position);

    /**
     * Get the tile at the given position in this level.
     *
     *
     * @param x The x coordinate of the position of the tile to get.
     * @param y The y coordinate of the position of the tile to get.
     *
     * @return {@code null} if the position is outside of the bounds of this level.
     */
    Optional<? extends ITile> getTile(int x, int y);
}
