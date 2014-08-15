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

import static com.lyndir.lhunath.opal.system.util.ObjectUtils.*;

import com.google.common.collect.*;
import com.lyndir.lhunath.opal.math.Vec2;
import com.lyndir.lhunath.opal.system.error.TodoException;
import com.lyndir.lhunath.opal.system.util.ObjectMeta;
import com.lyndir.omicron.api.util.Maybe;
import java.util.*;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
@ObjectMeta(useFor = { })
public class Tile extends ThriftObject<com.lyndir.omicron.api.thrift.Tile> implements ITile {

    public Tile(final com.lyndir.omicron.api.thrift.Tile thrift) {
        super( thrift );
    }

    @Override
    public int hashCode() {
        return Objects.hash( getPosition(), getLevel() );
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof Tile))
            return false;

        Tile o = (Tile) obj;
        return isEqual( getPosition(), o.getPosition() ) && isEqual( getLevel(), o.getLevel() );
    }

    @Override
    public Maybe<? extends IGameObject> getContents() {
        throw new TodoException();
    }

    @Override
    public Vec2 getPosition() {
        return cast( thrift().getPosition() );
    }

    @Override
    public ILevel getLevel() {
        throw new TodoException();
    }

    @Override
    public ImmutableMap<ResourceType, Maybe<Integer>> getQuantitiesByResourceType() {
        ImmutableMap.Builder<ResourceType, Maybe<Integer>> builder = ImmutableMap.builder();
        thrift().getQuantitiesByResourceType().forEach( (resourceType, maybeI16) -> builder.put( cast( resourceType ), cast( maybeI16 ) ) );
        return builder.build();
    }

    @Override
    public Optional<? extends IPlayer> getOwner() {
        throw new TodoException();
    }

    @Override
    public Maybe<? extends ITile> getLocation() {
        throw new TodoException();
    }
}
