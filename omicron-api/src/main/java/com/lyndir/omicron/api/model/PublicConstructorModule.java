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
import com.lyndir.omicron.api.Authenticated;
import com.lyndir.omicron.api.model.error.OmicronException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javax.annotation.Nonnull;


public class PublicConstructorModule extends PublicModule implements IConstructorModule {

    private final IConstructorModule core;

    protected PublicConstructorModule(final IConstructorModule core) {
        super( core );

        this.core = core;
    }

    @Override
    public PublicModuleType<?> getBuildsModule()
            throws Security.NotAuthenticatedException, Security.NotObservableException {
        assertObservable();

        return core.getBuildsModule();
    }

    @Override
    public int getBuildSpeed()
            throws Security.NotAuthenticatedException, Security.NotObservableException {
        assertObservable();

        return core.getBuildSpeed();
    }

    @Override
    public boolean isResourceConstrained()
            throws Security.NotAuthenticatedException, Security.NotObservableException {
        assertObservable();

        return core.isResourceConstrained();
    }

    @Override
    public int getRemainingSpeed()
            throws Security.NotAuthenticatedException, Security.NotObservableException {
        assertObservable();

        return core.getRemainingSpeed();
    }

    @Override
    public IGameObject getTarget()
            throws Security.NotAuthenticatedException, Security.NotObservableException {
        assertObservable();

        return core.getTarget();
    }

    @Override
    @Authenticated
    public void setTarget(final IGameObject target)
            throws Security.NotOwnedException, Security.NotAuthenticatedException, Security.NotObservableException {
        assertOwned();
        Security.assertOwned( target );

        core.setTarget( target );
    }

    @Override
    public PublicModuleType<? extends IConstructorModule> getType() {
        return PublicModuleType.CONSTRUCTOR;
    }

    @Override
    public ImmutableSet<? extends IUnitType> blueprints() {
        return core.blueprints();
    }

    /**
     * Schedule the construction of a new unit of the given type on the given location.
     *
     * @param unitType The type of unit to construct.
     * @param location The location to construct the new unit.  It must be accessible and adjacent to this module's game object.
     *
     * @return The job that will be created for the construction of the new unit.
     */
    @Override
    @Authenticated
    public IConstructionSite schedule(final IUnitType unitType, final ITile location)
            throws Security.NotOwnedException, Security.NotAuthenticatedException, Security.NotObservableException,
                   IConstructorModule.OutOfRangeException, IConstructorModule.InaccessibleException,
                   IConstructorModule.IncompatibleLevelException {
        assertOwned();
        Security.assertObservable( location );

        return core.schedule( unitType, location );
    }

    /**
     * A construction site is a unit that is under construction.  Its controller manages its construction progress and it turns into the
     * constructed unit upon completion.
     */
    @SuppressFBWarnings({ "EQ_DOESNT_OVERRIDE_EQUALS" })
    public static class PublicConstructionSite extends PublicGameObject implements IConstructionSite {

        private final IConstructionSite core;

        PublicConstructionSite(final IConstructionSite core) {
            super( core );

            this.core = core;
        }

        @Override
        public int getRemainingWork(final PublicModuleType<?> moduleType) {
            return core.getRemainingWork( moduleType );
        }

        @Override
        public ImmutableResourceCost getRemainingResourceCost() {
            return core.getRemainingResourceCost();
        }

        @Override
        public Optional<ImmutableResourceCost> getResourceCostToPerformWork(final PublicModuleType<?> moduleType) {
            return core.getResourceCostToPerformWork( moduleType );
        }

        @Nonnull
        @Override
        public IGameObjectController<? extends IGameObject> getController() {
            return core.getController();
        }
    }


    public static class InaccessibleException extends OmicronException {

        InaccessibleException() {
            super( "Location is inaccessible." );
        }
    }


    public static class IncompatibleLevelException extends OmicronException {

        IncompatibleLevelException() {
            super( "Target location's level is incompatible with the current state." );
        }
    }


    public static class OutOfRangeException extends OmicronException {

        OutOfRangeException() {
            super( "Target location is out of range." );
        }
    }
}
