package com.lyndir.omnicron.api.model;

import com.google.common.base.Optional;
import com.google.common.collect.*;
import com.lyndir.lhunath.opal.system.util.MetaObject;
import com.lyndir.lhunath.opal.system.util.ObjectMeta;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.Nullable;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
public class Level extends MetaObject {

    private final String name;
    private final Size   levelSize;

    @ObjectMeta(ignoreFor = ObjectMeta.For.all)
    private final Map<Coordinate, Tile> tileMap = Maps.newHashMap();

    public Level(final String name, final Size levelSize) {

        this.name = name;
        this.levelSize = levelSize;
    }

    public String getName() {

        return name;
    }

    public Size getLevelSize() {

        return levelSize;
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

        if (!levelSize.isInBounds( position ))
            return Optional.absent();

        Tile tile = tileMap.get( position );

        if (tile == null)
            tile = new Tile( position, this );

        return Optional.of( tile );
    }

    @SafeVarargs
    public static Set<Class<? extends Level>> set(final Class<? extends Level>... levelTypes) {

        return ImmutableSet.copyOf( levelTypes );
    }

    public static <V> Map<Class<? extends Level>, V> map(final Class<? extends Level> levelType1, final V value1) {

        return ImmutableMap.<Class<? extends Level>, V>of( levelType1, value1 );
    }

    public static <V> Map<Class<? extends Level>, V> map(final Class<? extends Level> levelType1, final V value1,
                                                         final Class<? extends Level> levelType2, final V value2) {

        return ImmutableMap.of( levelType1, value1, levelType2, value2 );
    }

    public static <V> Map<Class<? extends Level>, V> map(final Class<? extends Level> levelType1, final V value1,
                                                         final Class<? extends Level> levelType2, final V value2,
                                                         final Class<? extends Level> levelType3, final V value3) {

        return ImmutableMap.of( levelType1, value1, levelType2, value2, levelType3, value3 );
    }
}
