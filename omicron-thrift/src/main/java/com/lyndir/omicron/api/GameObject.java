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

import com.google.common.collect.*;
import com.lyndir.lhunath.opal.system.error.TodoException;
import com.lyndir.omicron.api.util.Maybe;
import java.util.Objects;
import javax.annotation.Nonnull;


/**
 * <i>10 15, 2012</i>
 *
 * @author lhunath
 */
public class GameObject extends ThriftObject<com.lyndir.omicron.api.thrift.GameObject> implements IGameObject {

    public GameObject(final com.lyndir.omicron.api.thrift.GameObject thrift) {
        super( thrift );
    }

    @Override
    public int hashCode() {
        return Objects.hashCode( getObjectID() );
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof IGameObject && getObjectID() == ((IGameObject) obj).getObjectID();
    }

    @Override
    public long getObjectID() {
        return thrift().getObjectID();
    }

    @Override
    public IGame getGame() {
        throw new TodoException();
    }

    @Override
    public IUnitType getType() {
        return cast( thrift().getType() );
    }

    @Override
    public ImmutableMultimap<PublicModuleType<?>, ? extends IModule> getModulesByType() {
        // TODO
        return null;
    }

    @Nonnull
    @Override
    public IGameObjectController<? extends IGameObject> getController() {
        throw new TodoException();
    }

    @Override
    public java.util.Optional<? extends IPlayer> getOwner() {
        throw new TodoException();
    }

    @Override
    public Maybe<? extends ITile> getLocation() {
        return cast( thrift().getLocation() );
    }
}
