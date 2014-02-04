package com.lyndir.omicron.api.model;

import com.google.common.base.Optional;
import com.lyndir.lhunath.opal.math.Size;
import com.lyndir.lhunath.opal.math.Vec2;
import com.lyndir.lhunath.opal.system.util.MetaObject;
import java.util.Map;
import javax.annotation.Nullable;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
public class PublicLevel extends MetaObject implements ILevel {

    private final ILevel core;

    PublicLevel(final ILevel core) {
        this.core = core;
    }

    @Override
    public int hashCode() {
        return core.hashCode();
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        if (obj instanceof PublicLevel)
            return core.equals( ((PublicLevel) obj).core );

        return core.equals( obj );
    }

    @Override
    public Size getSize() {
        return core.getSize();
    }

    @Override
    public LevelType getType() {
        return core.getType();
    }

    @Override
    public IGame getGame() {
        return core.getGame();
    }

    @Override
    public Map<Vec2, ? extends ITile> getTiles() {
        return core.getTiles();
    }

    /**
     * Get the tile at the given position in this level.
     *
     *
     *
     * @param position The position of the tile to get.
     *
     * @return {@code null} if the position is outside of the bounds of this level.
     */
    @Override
    public Optional<? extends ITile> getTile(final Vec2 position) {
        return core.getTile( position );
    }

    /**
     * Get the tile at the given position in this level.
     *
     *
     * @param x The x coordinate of the position of the tile to get.
     * @param y The y coordinate of the position of the tile to get.
     *
     * @return {@code null} if the position is outside of the bounds of this level.
     */
    @Override
    public Optional<? extends ITile> getTile(final int x, final int y) {
        return core.getTile( x, y );
    }
}
