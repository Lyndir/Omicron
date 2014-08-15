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


public class BaseModule extends Module<com.lyndir.omicron.api.thrift.BaseModule> implements IBaseModule {

    protected BaseModule(final com.lyndir.omicron.api.thrift.BaseModule thrift) {
        super( thrift );
    }

    @Override
    protected com.lyndir.omicron.api.thrift.Module thriftModule() {
        return thrift().getZuper();
    }

    @Override
    public int getMaxHealth() {
        return thrift().getMaxHealth();
    }

    @Override
    public int getDamage() {
        return thrift().getDamage();
    }

    @Override
    public int getArmor() {
        return thrift().getArmor();
    }

    @Override
    public int getViewRange() {
        return thrift().getViewRange();
    }

    @Override
    public ImmutableSet<LevelType> getSupportedLayers() {
        return FluentIterable.from(thrift().getSupportedLayers()).transform( this::cast ).toSet();
    }

    @Override
    public IBaseModuleController getController() {
        throw new TodoException();
    }
}
