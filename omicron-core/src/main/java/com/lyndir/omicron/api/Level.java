package com.lyndir.omicron.api;

import com.google.common.collect.ImmutableMap;
import com.lyndir.lhunath.opal.math.Size;
import com.lyndir.lhunath.opal.math.Vec2;
import com.lyndir.lhunath.opal.system.util.*;
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
    private final ImmutableMap<Vec2, Tile> tileMap;

    Level(final Size size, final LevelType type) {
        this.size = size;
        this.type = type;

        ImmutableMap.Builder<Vec2, Tile> tileMapBuilder = ImmutableMap.builder();
        for (int x = 0; x < size.getWidth(); ++x)
            for (int y = 0; y < size.getHeight(); ++y) {
                Vec2 coordinate = Vec2.create( x, y );
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
    public ImmutableMap<Vec2, ? extends ITile> getTilesByPosition() {
        return tileMap;
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
        return size.equals( o.size ) && type == o.type;
    }
}
