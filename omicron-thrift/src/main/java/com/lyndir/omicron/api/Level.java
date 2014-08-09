/*
 * Copyright 2010, Maarten Billemont
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.lyndir.omicron.api;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.lyndir.lhunath.opal.math.Size;
import com.lyndir.lhunath.opal.math.Vec2;
import com.lyndir.lhunath.opal.system.util.*;
import com.lyndir.omicron.api.core.ILevel;
import com.lyndir.omicron.api.core.LevelType;
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
    private final Size                                  size;
    private final com.lyndir.omicron.api.core.LevelType type;
    @ObjectMeta(ignoreFor = ObjectMeta.For.all)
    private final Game                                  game;

    @ObjectMeta(ignoreFor = ObjectMeta.For.all)
    private final ImmutableMap<Vec2, Tile> tileMap;

    Level(final Size size, final com.lyndir.omicron.api.core.LevelType type, final Game game) {
        this.size = size;
        this.type = type;
        this.game = game;

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
    public Game getGame() {
        return game;
    }

    @Override
    public Map<Vec2, Tile> getTiles() {
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
    public Optional<Tile> getTile(final Vec2 position) {
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
        return getTile( Vec2.create( x, y ) );
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
