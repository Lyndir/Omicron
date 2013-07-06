package com.lyndir.omnicron.api;

import com.google.common.collect.Maps;
import com.lyndir.lhunath.opal.system.util.MetaObject;
import java.util.Map;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
public class Level extends MetaObject {

    private final Size levelSize;
    private final Map<Coordinate, Tile> tileMap = Maps.newHashMap();

    public Level(final Size levelSize) {

        this.levelSize = levelSize;
    }

    public Size getLevelSize() {

        return levelSize;
    }

    public void putTile(final Coordinate position, final Tile tile) {

        tileMap.put( position, tile );
    }

    public Tile getTile(final Coordinate position) {

        Tile tile = tileMap.get( position );

        if (tile == null)
            tile = new Tile( position, this );

        return tile;
    }
}
