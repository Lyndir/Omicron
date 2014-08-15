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
import com.lyndir.lhunath.opal.math.Vec2;
import com.lyndir.lhunath.opal.math.Size;
import com.lyndir.omicron.api.thrift.*;
import com.lyndir.omicron.api.thrift.ResourceCost;
import com.lyndir.omicron.api.util.Maybe;


/**
 * @author lhunath, 2014-08-07
 */
public class ThriftObject<T> {

    final T thrift;

    protected ThriftObject(final T thrift) {
        this.thrift = thrift;
    }

    public T thrift() {
        return thrift;
    }

    com.lyndir.omicron.api.thrift.LevelType cast(final LevelType levelType) {
        return com.lyndir.omicron.api.thrift.LevelType.values()[levelType.ordinal()];
    }

    LevelType cast(final com.lyndir.omicron.api.thrift.LevelType levelType) {
        return LevelType.values()[levelType.ordinal()];
    }

    Turn cast(final com.lyndir.omicron.api.thrift.Turn turn) {
        return new Turn( turn.getNumber() );
    }

    ResourceType cast(final com.lyndir.omicron.api.thrift.ResourceType resourceType) {
        return ResourceType.values()[resourceType.ordinal()];
    }

    Maybe<Integer> cast(final MaybeI16 maybeI16) {
        if (!maybeI16.isKnown())
            return Maybe.unknown();

        if (!maybeI16.isSetValue())
            return Maybe.empty();

        return Maybe.of( (int) maybeI16.getValue() );
    }

    Maybe<Tile> cast(final MaybeTile maybeTile) {
        if (!maybeTile.isKnown())
            return Maybe.unknown();

        if (!maybeTile.isSetValue())
            return Maybe.empty();

        return Maybe.of( new Tile( maybeTile.getValue() ) );
    }

    PublicUnitTypes cast(final UnitType unitType) {
        return PublicUnitTypes.values()[unitType.ordinal()];
    }

    Vec2 cast(final com.lyndir.omicron.api.thrift.Vec2 vec2) {
        return Vec2.create( vec2.getX(), vec2.getY() );
    }

    Size cast(final com.lyndir.omicron.api.thrift.Size size) {
        return new Size( size.getWidth(), size.getHeight() );
    }

    ImmutableResourceCost cast(final ResourceCost resourceCost) {
        ImmutableMap.Builder<ResourceType, Integer> builder = ImmutableBiMap.builder();
        resourceCost.getQuantitiesByResourceType().forEach( (resourceType, quantity) -> {
            builder.put( cast( resourceType ), (int) quantity );
        } );
        return new ImmutableResourceCost( builder.build() );
    }
}
