package com.lyndir.omnicron.api.model;

import com.google.common.base.Objects;
import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.system.util.*;
import org.jetbrains.annotations.Nullable;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
@ObjectMeta(useFor = { })
public class Tile extends MetaObject {

    private static final Logger logger = Logger.get( Tile.class );

    @Nullable
    private       GameObject contents;
    @ObjectMeta(useFor = ObjectMeta.For.all)
    private final Coordinate position;
    @Nullable
    private final Tile       northWest;
    @Nullable
    private final Tile       northEast;
    @Nullable
    private final Tile       west;
    @Nullable
    private final Tile       east;
    @Nullable
    private final Tile       southWest;
    @Nullable
    private final Tile       southEast;
    @ObjectMeta(useFor = ObjectMeta.For.all)
    private final Level      level;

    public Tile(final Coordinate position, final Level level) {

        this.position = position;
        this.level = level;

        level.putTile( position, this );

        northWest = level.getTile( position.getNW( level.getLevelSize() ) );
        northEast = level.getTile( position.getNE( level.getLevelSize() ) );
        west = level.getTile( position.getW( level.getLevelSize() ) );
        east = level.getTile( position.getE( level.getLevelSize() ) );
        southWest = level.getTile( position.getSW( level.getLevelSize() ) );
        southEast = level.getTile( position.getSE( level.getLevelSize() ) );
    }

    @Nullable
    public GameObject getContents() {

        return contents;
    }

    public void setContents(@Nullable final GameObject contents) {

        this.contents = contents;
    }

    public Coordinate getPosition() {

        return position;
    }

    public Level getLevel() {

        return level;
    }

    @Nullable
    public Tile getNorthWest() {

        return northWest;
    }

    @Nullable
    public Tile getNorthEast() {

        return northEast;
    }

    @Nullable
    public Tile getWest() {

        return west;
    }

    @Nullable
    public Tile getEast() {

        return east;
    }

    @Nullable
    public Tile getSouthWest() {

        return southWest;
    }

    @Nullable
    public Tile getSouthEast() {

        return southEast;
    }

    public boolean contains(final GameObserver target) {

        for (GameObserver candidate = contents; candidate != null; candidate = candidate.getParent())
            if (candidate == target)
                return true;

        return false;
    }

    @Override
    public int hashCode() {

        return Objects.hashCode( position, level );
    }

    @Override
    public boolean equals(final Object obj) {

        if (!(obj instanceof Tile))
            return false;

        Tile o = (Tile) obj;
        return ObjectUtils.isEqual( position, o.position ) && ObjectUtils.isEqual( level, o.level );
    }
}
