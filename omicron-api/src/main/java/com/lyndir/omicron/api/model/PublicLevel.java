package com.lyndir.omicron.api.model;

import com.google.common.base.Optional;
import com.lyndir.lhunath.opal.system.util.MetaObject;
import java.util.Map;


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
    public Map<Coordinate, ? extends ITile> getTiles() {
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
    public Optional<? extends ITile> getTile(final Coordinate position) {
        return core.getTile( position );
    }

    /**
     * Get the tile at the given position in this level.
     *
     *
     * @param u The u coordinate of the position of the tile to get.
     * @param v The v coordinate of the position of the tile to get.
     *
     * @return {@code null} if the position is outside of the bounds of this level.
     */
    @Override
    public Optional<? extends ITile> getTile(final int u, final int v) {
        return core.getTile( u, v );
    }
}
