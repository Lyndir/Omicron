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


public class MobilityModule extends Module<com.lyndir.omicron.api.thrift.MobilityModule> implements IMobilityModule {

    public MobilityModule(final com.lyndir.omicron.api.thrift.MobilityModule thrift) {
        super( thrift );
    }

    @Override
    protected com.lyndir.omicron.api.thrift.Module thriftModule() {
        return thrift().getZuper();
    }

    @Override
    public double getMovementSpeed() {
        return thrift().getMovementSpeed();
    }

    @Override
    public double getRemainingSpeed() {
        return thrift().getRemainingSpeed();
    }

    @Override
    public double costForMovingInLevel(final LevelType levelType) {
        return thrift().getCostForMovementInLevelType().get( cast( levelType ) );
    }

    @Override
    public double costForLevelingToLevel(final LevelType levelType) {
        return thrift().getCostForLevelingToLevelType().get( cast( levelType ) );
    }

    @Override
    public IMobilityModuleController getController() {
        throw new TodoException();
    }
}
