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

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.lyndir.omicron.api.model.error.OmicronException;


public interface IConstructorModule extends IModule {

    @Override
    PublicModuleType<? extends IConstructorModule> getType();

    PublicModuleType<?> getBuildsModule()
            throws Security.NotAuthenticatedException, Security.NotObservableException;

    int getBuildSpeed()
            throws Security.NotAuthenticatedException, Security.NotObservableException;

    boolean isResourceConstrained()
            throws Security.NotAuthenticatedException, Security.NotObservableException;

    int getRemainingSpeed()
            throws Security.NotAuthenticatedException, Security.NotObservableException;

    IGameObject getTarget()
            throws Security.NotAuthenticatedException, Security.NotObservableException;

    void setTarget(final IGameObject target)
            throws Security.NotOwnedException, Security.NotAuthenticatedException, Security.NotObservableException;

    ImmutableSet<? extends IUnitType> blueprints();

    /**
     * Schedule the construction of a new unit of the given type on the given location.
     *
     * @param unitType The type of unit to construct.
     * @param location The location to construct the new unit.  It must be accessible and adjacent to this module's game object.
     *
     * @return The job that will be created for the construction of the new unit.
     */
    IConstructionSite schedule(final IUnitType unitType, final ITile location)
            throws Security.NotOwnedException, Security.NotAuthenticatedException, Security.NotObservableException, InaccessibleException,
                   IncompatibleLevelException, OutOfRangeException;

    /**
     * A construction site is a unit that is under construction.  Its controller manages its construction progress and it turns into the
     * constructed unit upon completion.
     */
    interface IConstructionSite extends IGameObject {

        int getRemainingWork(final PublicModuleType<?> moduleType);

        ImmutableResourceCost getRemainingResourceCost();

        Optional<ImmutableResourceCost> getResourceCostToPerformWork(final PublicModuleType<?> moduleType);
    }


    class InaccessibleException extends OmicronException {

        InaccessibleException() {
            super( "Location is inaccessible." );
        }
    }


    class IncompatibleLevelException extends OmicronException {

        IncompatibleLevelException() {
            super( "Target location's level is incompatible with the current state." );
        }
    }


    class OutOfRangeException extends OmicronException {

        OutOfRangeException() {
            super( "Target location is out of range." );
        }
    }
}
