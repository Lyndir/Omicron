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

import com.google.common.collect.ImmutableSet;
import com.lyndir.lhunath.opal.system.error.AlreadyCheckedException;
import com.lyndir.lhunath.opal.system.error.TodoException;
import com.lyndir.omicron.api.error.NotAuthenticatedException;
import com.lyndir.omicron.api.error.NotObservableException;


public class ConstructorModule extends Module<com.lyndir.omicron.api.thrift.ConstructorModule> implements IConstructorModule {

    protected ConstructorModule(final com.lyndir.omicron.api.thrift.ConstructorModule thrift) {
        super( thrift );
    }

    @Override
    protected com.lyndir.omicron.api.thrift.Module thriftModule() {
        return thrift().getZuper();
    }

    @Override
    public PublicModuleType<?> getBuildsModule() {
        switch (thrift().getBuildsModule()) {
            case M_BASE:
                return PublicModuleType.BASE;
            case M_MOBILITY:
                return PublicModuleType.MOBILITY;
            case M_CONTAINER:
                return PublicModuleType.CONTAINER;
            case M_EXTRACTOR:
                return PublicModuleType.EXTRACTOR;
            case M_CONSTRUCTOR:
                return PublicModuleType.CONSTRUCTOR;
            case M_WEAPON:
                return PublicModuleType.WEAPON;
        }

        throw new AlreadyCheckedException();
    }

    @Override
    public int getBuildSpeed()
            throws NotAuthenticatedException, NotObservableException {
        return thrift().getBuildSpeed();
    }

    @Override
    public boolean isResourceConstrained()
            throws NotAuthenticatedException, NotObservableException {
        return thrift().isResourceConstrained();
    }

    @Override
    public int getRemainingSpeed()
            throws NotAuthenticatedException, NotObservableException {
        return thrift().getRemainingSpeed();
    }

    @Override
    public IGameObject getTarget()
            throws NotAuthenticatedException, NotObservableException {
        return new GameObject( thrift().getTarget() );
    }

    @Override
    public ImmutableSet<? extends IUnitType> blueprints() {
        return ImmutableSet.copyOf( thrift().getBlueprints().stream().map( this::cast ).iterator() );
    }

    @Override
    public IConstructorModuleController getController() {
        throw new TodoException();
    }
}
