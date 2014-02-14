package com.lyndir.omicron.api.model;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.lyndir.lhunath.opal.math.Size;
import com.lyndir.lhunath.opal.math.Vec2Hex;
import com.lyndir.lhunath.opal.system.util.*;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
public class Level extends MetaObject implements ILevel {

    @ObjectMeta(ignoreFor = ObjectMeta.For.toString)
    private final Size      size;
    private final LevelType type;
    @ObjectMeta(ignoreFor = ObjectMeta.For.all)
    private final Game      game;

    @ObjectMeta(ignoreFor = ObjectMeta.For.all)
    private final ImmutableMap<Vec2Hex, Tile> tileMap;

    Level(final Size size, final LevelType type, final Game game) {
        this.size = size;
        this.type = type;
        this.game = game;

        ImmutableMap.Builder<Vec2Hex, Tile> tileMapBuilder = ImmutableMap.builder();
        for (int x = 0; x < size.getWidth(); ++x)
            for (int y = 0; y < size.getHeight(); ++y) {
                Vec2Hex coordinate = new Vec2Hex( x, y, size );
                tileMapBuilder.put( coordinate, new Tile( coordinate, this ) );
            }
        tileMap = tileMapBuilder.build();
    }

    @Override
    public Size getSize() {
        return size;
    }

    @Override
    public LevelType getType() {
        return type;
    }

    @Override
    public Game getGame() {
        return game;
    }

    @Override
    public Map<Vec2Hex, Tile> getTiles() {
        return tileMap;
    }

    /**
     * Get the tile at the given position in this level.
     *
     * @param position The position of the tile to get.
     *
     * @return {@code null} if the position is outside of the bounds of this level.
     */
    @Override
    public Optional<Tile> getTile(final Vec2Hex position) {
        if (!size.isInBounds( position ))
            return Optional.absent();

        return Optional.of( tileMap.get( position ) );
    }

    /**
     * Get the tile at the given position in this level.
     *
     * @param x The x coordinate of the position of the tile to get.
     * @param y The y coordinate of the position of the tile to get.
     *
     * @return {@code null} if the position is outside of the bounds of this level.
     */
    @Override
    public Optional<Tile> getTile(final int x, final int y) {
        return getTile( new Vec2Hex( x, y, getSize() ) );
    }

    @Override
    public int hashCode() {
        return Objects.hash( size, type );
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof Level))
            return false;

        Level o = (Level) obj;
        return ObjectUtils.isEqual( size, o.size ) && ObjectUtils.isEqual( type, o.type );
    }
}
