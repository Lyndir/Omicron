package com.lyndir.omnicron.api.model;

import com.google.common.collect.Maps;
import com.lyndir.lhunath.opal.system.util.MetaObject;
import com.lyndir.lhunath.opal.system.util.ObjectMeta;
import java.util.Map;
import org.jetbrains.annotations.Nullable;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
public class Level extends MetaObject {

    private final Size levelSize;

    @ObjectMeta(ignoreFor = ObjectMeta.For.all)
    private final Map<Coordinate, Tile> tileMap = Maps.newHashMap();

    public Level(final Size levelSize) {

        this.levelSize = levelSize;
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

    @Nullable
    public Tile getTile(final Coordinate position) {

        if (!levelSize.isInBounds( position ))
            return null;

        Tile tile = tileMap.get( position );

        if (tile == null)
            tile = new Tile( position, this );

        return tile;
    }
}
