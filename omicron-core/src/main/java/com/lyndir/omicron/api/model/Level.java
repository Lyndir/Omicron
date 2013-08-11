package com.lyndir.omicron.api.model;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.lyndir.lhunath.opal.system.util.*;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
public class Level extends MetaObject {

    @ObjectMeta(ignoreFor = ObjectMeta.For.toString)
    private final Size      size;
    private final LevelType type;
    @ObjectMeta(ignoreFor = ObjectMeta.For.all)
    private final Game      game;

    @ObjectMeta(ignoreFor = ObjectMeta.For.all)
    private final ImmutableMap<Coordinate, Tile> tileMap;

    Level(final Size size, final LevelType type, final Game game) {

        this.size = size;
        this.type = type;
        this.game = game;

        ImmutableMap.Builder<Coordinate, Tile> tileMapBuilder = ImmutableMap.builder();
        for (int u = 0; u < size.getWidth(); ++u)
            for (int v = 0; v < size.getHeight(); ++v) {
                Coordinate coordinate = new Coordinate( u, v, size );
                tileMapBuilder.put( coordinate, new Tile( coordinate, this ) );
            }
        tileMap = tileMapBuilder.build();
    }

    public Size getSize() {

        return size;
    }

    public LevelType getType() {

        return type;
    }

    public Game getGame() {

        return game;
    }

    Map<Coordinate, Tile> getTiles() {

        return tileMap;
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

        return Optional.of( tileMap.get( position ) );
    }

    /**
     * Get the tile at the given position in this level.
     *
     * @param u The u coordinate of the position of the tile to get.
     * @param v The v coordinate of the position of the tile to get.
     *
     * @return {@code null} if the position is outside of the bounds of this level.
     */
    Optional<Tile> getTile(final int u, final int v) {

        return getTile( new Coordinate( u, v, getSize() ) );
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
