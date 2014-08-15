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
import com.lyndir.lhunath.opal.system.error.TodoException;


public class WeaponModule extends Module<com.lyndir.omicron.api.thrift.WeaponModule> implements IWeaponModule {

    public WeaponModule(final com.lyndir.omicron.api.thrift.WeaponModule thrift) {
        super( thrift );
    }

    @Override
    protected com.lyndir.omicron.api.thrift.Module thriftModule() {
        return thrift().getZuper();
    }

    @Override
    public int getFirePower() {
        return thrift().getFirePower();
    }

    @Override
    public int getVariance() {
        return thrift().getVariance();
    }

    @Override
    public int getRange() {
        return thrift().getRange();
    }

    @Override
    public int getRepeat() {
        return thrift().getRepeat();
    }

    @Override
    public int getAmmunitionLoad() {
        return thrift().getAmmunitionLoad();
    }

    @Override
    public int getRepeated() {
        return thrift().getRepeated();
    }

    @Override
    public int getAmmunition() {
        return thrift().getAmmunition();
    }

    @Override
    public ImmutableSet<LevelType> getSupportedLayers() {
        return ImmutableSet.copyOf( thrift().getSupportedLayers().stream().map( this::cast ).iterator() );
    }

    @Override
    public IWeaponModuleController getController() {
        throw new TodoException();
    }
}
