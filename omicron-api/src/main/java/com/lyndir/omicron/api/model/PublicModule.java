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

package com.lyndir.omicron.api.model;

import com.google.common.base.Preconditions;
import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.system.util.MetaObject;
import com.lyndir.lhunath.opal.system.util.ObjectMeta;
import javax.annotation.Nullable;


public abstract class PublicModule extends MetaObject implements IModule {

    @ObjectMeta(ignoreFor = ObjectMeta.For.all)
    protected final Logger logger = Logger.get( getClass() );
    private final IModule coreModule;

    protected PublicModule(final IModule coreModule) {
        this.coreModule = coreModule;

        Preconditions.checkState( getType().getModuleType().isInstance( this ), "Invalid module type for module: %s", this );
    }

    @Override
    public int hashCode() {
        return coreModule.hashCode();
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        if (obj instanceof PublicModule)
            return coreModule.equals( ((PublicModule) obj).coreModule );

        return coreModule.equals( obj );
    }

    @Override
    public ImmutableResourceCost getResourceCost()
            throws Security.NotAuthenticatedException, Security.NotObservableException {
        assertObservable();

        return coreModule.getResourceCost();
    }

    @Override
    public IGameObject getGameObject()
            throws Security.NotAuthenticatedException, Security.NotObservableException {
        assertObservable();

        return coreModule.getGameObject();
    }

    @Override
    public IGameController getGameController()
            throws Security.NotAuthenticatedException, Security.NotObservableException {
        assertObservable();

        return coreModule.getGameController();
    }

    void assertOwned()
            throws Security.NotAuthenticatedException, Security.NotObservableException, Security.NotOwnedException {
        Security.assertOwned( getGameObject() );
    }

    void assertObservable()
            throws Security.NotAuthenticatedException, Security.NotObservableException {
        Security.assertObservable( coreModule.getGameObject() );
    }
}
