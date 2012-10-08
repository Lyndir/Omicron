package com.lyndir.omnicron.api;

/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
public class Tile {

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
}
