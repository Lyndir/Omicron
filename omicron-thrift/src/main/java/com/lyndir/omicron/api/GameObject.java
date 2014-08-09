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

import static com.lyndir.omicron.api.core.Security.*;

import com.google.common.base.Optional;
import com.google.common.collect.*;
import com.lyndir.lhunath.opal.system.util.*;
import com.lyndir.omicron.api.core.*;
import com.lyndir.omicron.api.thrift.ThriftObject;
import com.lyndir.omicron.api.util.Maybe;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * <i>10 15, 2012</i>
 *
 * @author lhunath
 */
public class GameObject extends ThriftObject<com.lyndir.omicron.api.thrift.GameObject> implements IGameObject {

    @Override
    public int hashCode() {
        return getObjectID();
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof IGameObject && getObjectID() == ((IGameObject) obj).getObjectID();
    }

    @Override
    @Nonnull
    public GameObjectController<? extends GameObject> getController() {
        return controller;
    }

    @Override
    public int getObjectID() {
        return thrift().getObjectID();
    }

    @Override
    public Maybe<Tile> checkLocation()
            throws NotAuthenticatedException {
        Tile location = getLocation();
        if (!isGod() && !isOwnedByCurrentPlayer())
            if (!currentPlayer().canObserve( location ).isTrue())
                // Has a location but current player cannot observe it.
                return Maybe.unknown();

        // We're either god, can be observed by or are owned by the current player.
        return Maybe.of( location );
    }

    void setLocation(@Nonnull final Tile location) {
        Change.From<ITile> locationChange = Change.<ITile>from( this.location );
        this.location = location;

        getGame().getController().fireIfObservable( location ) //
                .onUnitMoved( this, locationChange.to( this.location ) );
    }

    @Override
    public UnitType getType() {
        return unitType;
    }

    @Override
    public <M extends IModule> Optional<M> getModule(final PublicModuleType<M> moduleType, final int index)
            throws NotAuthenticatedException, NotObservableException {
        assertObservable( this );

        return Optional.fromNullable( Iterables.get( getModules( moduleType ), index, null ) );
    }

    @Override
    public <M extends IModule> Optional<M> getModule(final PublicModuleType<M> moduleType, final PredicateNN<M> predicate)
            throws NotAuthenticatedException, NotObservableException {
        assertObservable( this );

        return FluentIterable.from( getModules( moduleType ) ).firstMatch( predicate );
    }

    @Override
    public <M extends IModule> List<M> getModules(final PublicModuleType<M> moduleType)
            throws NotAuthenticatedException, NotObservableException {
        assertObservable( this );

        // Checked by Module's constructor.
        //noinspection unchecked
        return (List<M>) modules.get( coreMT( moduleType ) );
    }

    @Override
    public <M extends IModule> M onModuleElse(final PublicModuleType<M> moduleType, final int index, @Nullable final Object elseValue)
            throws NotAuthenticatedException, NotObservableException {
        assertObservable( this );

        return ObjectUtils.ifNotNullElse( moduleType.getModuleType(), getModule( moduleType, index ).orNull(), elseValue );
    }

    @Override
    public <M extends IModule> M onModuleElse(final PublicModuleType<M> moduleType, final PredicateNN<M> predicate,
                                              @Nullable final Object elseValue)
            throws NotAuthenticatedException, NotObservableException {
        assertObservable( this );

        return ObjectUtils.ifNotNullElse( moduleType.getModuleType(), getModule( moduleType, predicate ).orNull(), elseValue );
    }

    @Override
    public <M extends IModule> M onModule(final PublicModuleType<M> moduleType, final int index)
            throws NotAuthenticatedException, NotObservableException {
        assertObservable( this );

        return onModuleElse( moduleType, index, null );
    }

    @Override
    public <M extends IModule> M onModule(final PublicModuleType<M> moduleType, final PredicateNN<M> predicate)
            throws NotAuthenticatedException, NotObservableException {
        assertObservable( this );

        return onModuleElse( moduleType, predicate, null );
    }

    @Override
    public ImmutableCollection<Module> listModules()
            throws NotAuthenticatedException, NotObservableException {
        assertObservable( this );

        return ImmutableList.copyOf( modules.values() );
    }
}
