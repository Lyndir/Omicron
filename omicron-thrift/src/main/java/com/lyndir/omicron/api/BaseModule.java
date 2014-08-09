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

import com.google.common.base.Function;
import com.google.common.collect.*;
import com.lyndir.lhunath.opal.system.error.*;
import com.lyndir.omicron.api.core.*;
import com.lyndir.omicron.api.core.LevelType;
import com.lyndir.omicron.api.core.Security.*;
import com.lyndir.omicron.api.util.Maybool;
import javax.annotation.Nonnull;


public class BaseModule extends Module<com.lyndir.omicron.api.thrift.BaseModule> implements IBaseModule {

    protected BaseModule(final com.lyndir.omicron.api.thrift.BaseModule thrift) {
        super( thrift );
    }

    @Override
    protected com.lyndir.omicron.api.thrift.Module thriftModule() {
        return thrift().getZuper();
    }

    @Override
    public int getMaxHealth()
            throws NotAuthenticatedException, NotObservableException {
        return thrift().getMaxHealth();
    }

    @Override
    public int getRemainingHealth()
            throws NotAuthenticatedException, NotObservableException {
        return Math.max( 0, thrift().getMaxHealth() - thrift().getDamage() );
    }

    @Override
    public int getArmor()
            throws NotAuthenticatedException, NotObservableException {
        return thrift().getArmor();
    }

    @Override
    public int getViewRange()
            throws NotAuthenticatedException, NotObservableException {
        return thrift().getViewRange();
    }

    @Override
    public ImmutableSet<LevelType> getSupportedLayers()
            throws NotAuthenticatedException, NotObservableException {
        return FluentIterable.from(thrift().getSupportedLayers()).transform( new Function<com.lyndir.omicron.api.thrift.LevelType, LevelType>() {
            @Override
            public LevelType apply(final com.lyndir.omicron.api.thrift.LevelType input) {
                return LevelType.values()[input.ordinal()];
            }
        } ).toSet();
    }

    @Override
    public Maybool canObserve(@Nonnull final GameObservable observable)
            throws NotAuthenticatedException {
        // TODO
        throw new TodoException();
    }

    @Nonnull
    @Override
    public Iterable<? extends ITile> iterateObservableTiles()
            throws NotAuthenticatedException, NotObservableException {
        // TODO
        throw new TodoException();
    }
}
