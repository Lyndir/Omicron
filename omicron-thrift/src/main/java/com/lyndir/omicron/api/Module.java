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
import com.lyndir.lhunath.opal.system.logging.Logger;
import javax.annotation.Nullable;


public abstract class Module<T> extends ThriftObject<T> implements IModule {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger logger = Logger.get( Module.class );

    protected Module(final T thrift) {
        super( thrift );
    }

    protected abstract com.lyndir.omicron.api.thrift.Module thriftModule();

    @Override
    public int hashCode() {
        return System.identityHashCode( this );
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        return obj == this;
    }

    @Override
    public ImmutableResourceCost getResourceCost() {
        return cast( thriftModule().getResourceCost() );
    }

    @Override
    public GameObject getGameObject() {
        throw new TodoException();
    }
}
