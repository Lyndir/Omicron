package com.lyndir.omnicron.api;

import com.lyndir.lhunath.opal.system.util.MetaObject;
import org.jetbrains.annotations.Nullable;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
public class Tile extends MetaObject {

    @Nullable
    private       GameObject contents;
    private final Coordinate position;
    private final Tile       northWest;
    private final Tile       northEast;
    private final Tile       west;
    private final Tile       east;
    private final Tile       southWest;
    private final Tile       southEast;

    public Tile(final Coordinate position, final Level level) {

        this.position = position;

        level.putTile( position, this );

        northWest = level.getTile( position.getNW( level.getLevelSize() ) );
        northEast = level.getTile( position.getNE( level.getLevelSize() ) );
        west = level.getTile( position.getW( level.getLevelSize() ) );
        east = level.getTile( position.getE( level.getLevelSize() ) );
        southWest = level.getTile( position.getSW( level.getLevelSize() ) );
        southEast = level.getTile( position.getSE( level.getLevelSize() ) );
    }

    public Coordinate getPosition() {

        return position;
    }

    public Tile getNorthWest() {

        return northWest;
    }

    public Tile getNorthEast() {

        return northEast;
    }

    public Tile getWest() {

        return west;
    }

    public Tile getEast() {

        return east;
    }

    public Tile getSouthWest() {

        return southWest;
    }

    public Tile getSouthEast() {

        return southEast;
    }

    public boolean contains(final GameObserver target) {

        for (GameObserver candidate = contents; candidate != null; candidate = candidate.getParent())
            if (candidate == target)
                return true;

        return false;
    }
}
