package com.lyndir.omicron.api.model;

import com.google.common.base.Optional;
import com.google.common.collect.*;
import com.lyndir.lhunath.opal.system.util.*;
import java.util.*;
import org.jetbrains.annotations.Nullable;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
public class Level extends MetaObject {

    private final Size      size;
    private final LevelType type;

    @ObjectMeta(ignoreFor = ObjectMeta.For.all)
    private final Map<Coordinate, Tile> tileMap = Maps.newHashMap();

    public Level(final Size size, final LevelType type) {

        this.size = size;
        this.type = type;
    }

    public Size getSize() {

        return size;
    }

    public LevelType getType() {

        return type;
    }

    public Map<Coordinate, Tile> getTiles() {

        return tileMap;
    }

    public void putTile(final Coordinate position, final Tile tile) {

        tileMap.put( position, tile );
    }

    /**
     * Get the tile at the given position in this level.
     *
     * @param position The position of the tile to get.
     *
     * @return {@code null} if the position is outside of the bounds of this level.
     */
    public Optional<Tile> getTile(final Coordinate position) {

        if (!size.isInBounds( position ))
            return Optional.absent();

        Tile tile = tileMap.get( position );

        if (tile == null)
            tile = new Tile( position, this );

        return Optional.of( tile );
    }

    @Override
    public int hashCode() {

        return Objects.hash( size, type );
    }

    @Override
    public boolean equals(@Nullable final Object obj) {

        if (!(obj instanceof Level))
            return false;

        Level o = (Level) obj;
        return ObjectUtils.isEqual( size, o.size ) && ObjectUtils.isEqual( type, o.type );
    }
}
