package com.lyndir.omicron.api;

import com.google.common.collect.ImmutableMap;
import com.lyndir.lhunath.opal.math.Size;
import com.lyndir.lhunath.opal.math.Vec2;
import java.util.Optional;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
public interface ILevel {

    /**
     * @return The maximum dimensions for the tiles in this level.
     */
    Size getSize();

    /**
     * @return The type of level this level's tiles represent.
     */
    LevelType getType();

    /**
     * @return The tiles in this level mapped by their position.
     */
    ImmutableMap<Vec2, ? extends ITile> getTilesByPosition();

    /**
     * Get the tile at the given position in this level.
     *
     * @param position The position of the tile to get.
     *
     * @return {@code null} if the position is outside of the bounds of this level.
     */
    default Optional<? extends ITile> getTile(final Vec2 position){
        if (!getSize().isInBounds( position ))
            return Optional.empty();

        return Optional.of( getTilesByPosition().get( position ) );
    }
}
