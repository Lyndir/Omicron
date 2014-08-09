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

package com.lyndir.omicron.api.core;

import com.google.common.collect.ImmutableSet;


public interface IConstructorModule extends IModule {

    @Override
    default PublicModuleType<? extends IConstructorModule> getType() {
        return PublicModuleType.CONSTRUCTOR;
    }

    /**
     * @return The type of module this constructor is able to build.
     */
    PublicModuleType<?> getBuildsModule();

    /**
     * @return The speed at which the constructor is able to contribute to unit construction.
     */
    int getBuildSpeed();

    /**
     * @return Specifies whether the constructor was constrained during its previous construction effort due to insufficient resources.
     */
    boolean isResourceConstrained();

    /**
     * @return The amount of construction speed this unit is still able to contribute to unit construction in the current turn.
     */
    int getRemainingSpeed();

    /**
     * @return The unit that this constructor is currently targetting for contribution to construction.
     */
    IGameObject getTarget();

    /**
     * @return The unit types this constructor can create a new construction site for.
     */
    ImmutableSet<? extends IUnitType> blueprints();

    @Override
    IConstructorModuleController getController();
}
