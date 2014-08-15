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

import com.lyndir.lhunath.opal.system.error.TodoException;
import com.lyndir.omicron.api.error.NotAuthenticatedException;
import com.lyndir.omicron.api.error.NotObservableException;


public class ContainerModule extends Module<com.lyndir.omicron.api.thrift.ContainerModule> implements IContainerModule {

    protected ContainerModule(final com.lyndir.omicron.api.thrift.ContainerModule thrift) {
        super( thrift );
    }

    @Override
    protected com.lyndir.omicron.api.thrift.Module thriftModule() {
        return thrift().getZuper();
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.values()[thrift().getResourceType().ordinal()];
    }

    @Override
    public int getCapacity() {
        return thrift().getCapacity();
    }

    @Override
    public int getStock()
            throws NotAuthenticatedException, NotObservableException {
        return thrift().getStock();
    }

    @Override
    public IContainerModuleController getController() {
        throw new TodoException();
    }
}
