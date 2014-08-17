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

import com.google.common.collect.ImmutableBiMap;
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
public class Level extends ThriftObject<com.lyndir.omicron.api.thrift.Level> implements ILevel {

    public Level(final com.lyndir.omicron.api.thrift.Level thrift) {
        super( thrift );
    }

    @Override
    public Size getSize() {
        return cast( thrift().getSize() );
    }

    @Override
    public LevelType getType() {
        return cast( thrift().getType() );
    }

    @Override
    public ImmutableMap<Vec2, ? extends ITile> getTilesByPosition() {
        ImmutableMap.Builder<Vec2, ITile> builder = ImmutableBiMap.builder();
        thrift().getTilesByPosition().forEach( (position, tile) -> builder.put( cast( position ), new Tile( tile ) ) );
        return builder.build();
    }

    @Override
    public int hashCode() {
        return Objects.hash( getSize(), getType() );
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof Level))
            return false;

        Level o = (Level) obj;
        return getSize().equals( o.getSize() ) && getType() == o.getType();
    }
}
