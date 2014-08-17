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

import static com.lyndir.lhunath.opal.system.util.ObjectUtils.*;
import static com.lyndir.omicron.api.error.ExceptionUtils.assertState;

import com.google.common.collect.*;
import com.lyndir.lhunath.opal.system.util.*;
import com.lyndir.omicron.api.error.NotAuthenticatedException;
import com.lyndir.omicron.api.util.Maybe;
import com.lyndir.omicron.api.util.PathUtils;
import edu.umd.cs.findbugs.annotations.*;
import java.lang.SuppressWarnings;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class ConstructorModule extends Module implements IConstructorModule, IConstructorModuleController {

    private final int           buildSpeed;
    private final ModuleType<?> buildsModule;

    private boolean resourceConstrained;
    private int     remainingSpeed;

    @Nullable
    private GameObject target;

    protected ConstructorModule(final ImmutableResourceCost resourceCost, final int buildSpeed, final ModuleType<?> buildsModule) {
        super( resourceCost );

        this.buildSpeed = buildSpeed;
        this.buildsModule = buildsModule;
    }

    static Builder0 createWithStandardResourceCost() {
        return createWithExtraResourceCost( ImmutableResourceCost.immutable() );
    }

    static Builder0 createWithExtraResourceCost(final ImmutableResourceCost resourceCost) {
        return new Builder0( ModuleType.CONSTRUCTOR.getStandardCost().add( resourceCost ) );
    }

    @Override
    protected void onReset() {
        resourceConstrained = false;
        remainingSpeed = buildSpeed;
    }

    @Override
    protected void onNewTurn() {
    }

    // This method assumes a target link between this module and the site exists.
    private void construct(final ConstructionSite site) {
        if (isResourceConstrained() || remainingSpeed <= 0)
            return;

        ChangeInt.From remainingSpeedChange = ChangeInt.from( remainingSpeed );

        construction:
        for (; remainingSpeed > 0; --remainingSpeed) {
            /* Find resource cost */
            Optional<ImmutableResourceCost> resourceCostOptional = site.getResourceCostToPerformWork( getBuildsModule() );
            if (!resourceCostOptional.isPresent())
                // No work left to do.
                break;
            final MutableResourceCost resourceCost = ResourceCost.mutable( resourceCostOptional.get() );

            /* Find resource stock to cover cost */
            // Initialize path finding functions.
            PredicateNN<IGameObject> foundFunction = gameObject -> {
                for (final ContainerModule containerModule : gameObject.getModules( ModuleType.CONTAINER ))
                    if (resourceCost.get( containerModule.getResourceType() ) > 0 && containerModule.getStock() > 0)
                        return true;

                return false;
            };
            NNFunctionNN<PathUtils.Step<IGameObject>, Double> costFunction = gameObjectStep -> 1d;
            NNFunctionNN<IGameObject, Stream<? extends IGameObject>> neighboursFunction = gameObject -> {
                Maybe<? extends ITile> location = gameObject.getLocation();
                if (!location.isPresent())
                    return ImmutableList.<IGameObject>of().stream();

                return location.get().neighbours().stream() //
                        .map( new Function<ITile, IGameObject>() {
                            @Override
                            public IGameObject apply(final ITile gameObject_) {
                                return gameObject_.getContents().orNull();
                            }
                        } ) //
                        .filter( gameObject_ -> gameObject_ != null );
            };

            /* Find paths to containers and deposit mined resources. */
            ImmutableMap.Builder<ContainerModule, Integer> borrowedResources = ImmutableMap.builder();
            while (!resourceCost.isZero()) {
                Optional<PathUtils.Path<IGameObject>> path = PathUtils.find( getGameObject(), foundFunction, costFunction,
                                                                             Constants.MAX_DISTANCE_TO_CONTAINER, neighboursFunction );
                if (!path.isPresent()) {
                    resourceConstrained = true;
                    // No more containers with available stock: not enough resources available to complete work unit.
                    // Give borrowed resources back to containers.
                    for (final Map.Entry<ContainerModule, Integer> borrowEntry : borrowedResources.build().entrySet())
                        borrowEntry.getKey().addStock( borrowEntry.getValue() );

                    break construction;
                }

                for (final ContainerModule containerModule : path.get().getTarget().getModules( ModuleType.CONTAINER )) {
                    int moduleResourceCost = resourceCost.get( containerModule.getResourceType() );
                    int depletedStock = containerModule.depleteStock( moduleResourceCost );
                    borrowedResources.put( containerModule, depletedStock );
                    resourceCost.reduce( containerModule.getResourceType(), depletedStock );
                }
            }

            /* Complete a unit of work. */
            if (!site.performWork( getBuildsModule() ))
                // Failed to perform unit of work.  Shouldn't happen: this condition was tested at the beginning of the iteration.
                // Give borrowed resources back to containers.
                for (final Map.Entry<ContainerModule, Integer> borrowEntry : borrowedResources.build().entrySet())
                    borrowEntry.getKey().addStock( borrowEntry.getValue() );
        }

        getGameObject().getGame().getController().fireIfObservable( getGameObject() ) //
                .onConstructorWorked( this, remainingSpeedChange.to( remainingSpeed ) );
    }

    @Override
    public ModuleType<?> getBuildsModule() {
        return buildsModule;
    }

    @Override
    public int getBuildSpeed() {
        return buildSpeed;
    }

    @Override
    public boolean isResourceConstrained() {
        return resourceConstrained;
    }

    @Override
    public int getRemainingSpeed() {
        return remainingSpeed;
    }

    @Nullable
    @Override
    public GameObject getTarget() {
        return target;
    }

    @Override
    public void setTarget(@Nullable final IGameObject target) {
        Change.From<IGameObject> targetChange = Change.<IGameObject>from( this.target );

        this.target = GameObject.castN( target );

        Security.currentGame().getController().fireIfObservable( getGameObject() ) //
                .onConstructorTargeted( this, targetChange.to( this.target ) );
    }

    @Override
    public ImmutableSet<? extends UnitType> blueprints() {
        return ImmutableSet.copyOf( UnitTypes.values() );
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
    public ConstructionSite schedule(final IUnitType unitType, final ITile location)
            throws NotAuthenticatedException, InaccessibleException, IncompatibleLevelException, OutOfRangeException {
        Tile ownLocation = getGameObject().getLocation().get();
        assertState( location.isAccessible().isTrue(), InaccessibleException.class );
        assertState( location.getLevel().equals( ownLocation.getLevel() ), IncompatibleLevelException.class );
        assertState( location.getPosition().distanceTo( ownLocation.getPosition() ) == 1, OutOfRangeException.class );

        ConstructionSite site = new ConstructionSite( (UnitType) unitType, getGameObject().getGame(), getGameObject().getOwner().get(),
                                                      Tile.cast( location ) );
        site.register();
        setTarget( site );

        return site;
    }

    @Override
    public IConstructorModuleController getController() {
        return this;
    }

    @Override
    public IConstructorModule getModule() {
        return this;
    }

    /**
     * A construction site is a unit that is under construction.  Its controller manages its construction progress and it turns into the
     * constructed unit upon completion.
     */
    @SuppressFBWarnings({ "EQ_DOESNT_OVERRIDE_EQUALS" })
    public static class ConstructionSite extends GameObject implements IConstructionSite {

        private final UnitType constructionUnitType;
        private final Map<PublicModuleType<?>, Integer> remainingWork = Collections.synchronizedMap(
                Maps.<PublicModuleType<?>, Integer>newHashMap() );
        private final List<? extends Module> constructionModules;

        private ConstructionSite(@Nonnull final UnitType constructionUnitType, @Nonnull final Game game, @Nonnull final Player owner,
                                 final Tile location) {
            super( UnitTypes.CONSTRUCTION, game, owner, location );

            this.constructionUnitType = constructionUnitType;
            constructionModules = constructionUnitType.createModules();

            for (final Module module : constructionModules)
                remainingWork.put( module.getType(),
                                   ifNotNullElse( remainingWork.get( module.getType() ), 0 ) + constructionUnitType.getConstructionWork() );
        }

        @Override
        public int getRemainingWork(final PublicModuleType<?> moduleType) {
            return ifNotNullElse( remainingWork.get( moduleType ), 0 );
        }

        @Override
        public ImmutableResourceCost getRemainingResourceCost() {
            MutableResourceCost remainingResourceCost = ResourceCost.mutable();
            for (final Module constructionModule : constructionModules)
                remainingResourceCost.add(
                        constructionModule.getResourceCost().multiply( getRemainingWork( constructionModule.getType() ) ) );

            return ResourceCost.immutable( remainingResourceCost );
        }

        @Override
        public Optional<ImmutableResourceCost> getResourceCostToPerformWork(final PublicModuleType<?> moduleType) {
            for (final Module constructionModule : constructionModules)
                if (constructionModule.getType().equals( moduleType ) && getRemainingWork( constructionModule.getType() ) > 0)
                    return Optional.of( constructionModule.getResourceCost() );

            return Optional.empty();
        }

        /**
         * Reduce the amount of work left for building the modules of the given type by one unit.
         *
         * @param moduleType The module for which a constructor wants to complete a unit of work for.
         *
         * @return {@code true} if there was work left to complete for the given module type and a unit of work has now been completed.
         */
        private boolean performWork(final ModuleType<?> moduleType) {
            int remaining = getRemainingWork( moduleType );
            if (remaining > 0) {
                ChangeInt.From remainingWorkChange = ChangeInt.from( remainingWork.put( moduleType, --remaining ) );

                getGame().getController().fireIfObservable( this ) //
                        .onConstructionSiteWorked( this, moduleType, remainingWorkChange.to( remaining ) );

                return true;
            }

            return false;
        }

        @Nonnull
        @Override
        public GameObjectController<? extends GameObject> getController() {
            return new GameObjectController<GameObject>( this ) {

                @Override
                protected void onNewTurn() {
                    super.onNewTurn();

                    // Initialize path finding functions.
                    PredicateNN<IGameObject> foundFunction = gameObject -> {
                        for (final ConstructorModule module : gameObject.getModules( ModuleType.CONSTRUCTOR ))
                            if (module.getRemainingSpeed() > 0 && !module.isResourceConstrained()
                                && getRemainingWork( module.getBuildsModule() ) > 0)
                                return true;

                        return false;
                    };
                    NNFunctionNN<PathUtils.Step<IGameObject>, Double> costFunction = gameObjectStep -> 1d;
                    NNFunctionNN<IGameObject, Stream<? extends IGameObject>> neighboursFunction = neighbourInput -> {
                        ITile location = neighbourInput.getLocation().get();
                        return location.neighbours().stream().map( new Function<ITile, IGameObject>() {
                            @Override
                            public IGameObject apply(final ITile tile) {
                                Maybe<? extends IGameObject> contents = tile.getContents();
                                if (contents.isPresent())
                                    for (final ConstructorModule module : contents.get().getModules( ModuleType.CONSTRUCTOR ))
                                        if (neighbourInput.equals( module.getTarget() ))
                                            return contents.get();

                                return null;
                            }
                        } ).filter( gameObject -> gameObject != null );
                    };

                    // Find paths to constructor and use them to work on the job.
                    while (true) {
                        Optional<PathUtils.Path<IGameObject>> path = PathUtils.find( getGameObject(), foundFunction, costFunction,
                                                                                    Constants.MAX_DISTANCE_TO_CONSTRUCTOR,
                                                                                    neighboursFunction );
                        if (!path.isPresent())
                            // No more constructors with remaining speed or construction finished.
                            break;

                        for (final ConstructorModule constructorModule : path.get().getTarget().getModules( ModuleType.CONSTRUCTOR ))
                            constructorModule.construct( ConstructionSite.this );
                    }

                    // Check if we managed to complete all the work.
                    synchronized (remainingWork) {
                        if (FluentIterable.from( remainingWork.values() ).filter( remainingWork1 -> remainingWork1 > 0 ).isEmpty())
                            // No more work remaining; create the constructed unit.
                            replaceWith( new GameObject( constructionUnitType, getGame(), getOwner().get(), getLocation().get() ) );
                    }
                }
            };
        }
    }


    @SuppressWarnings({ "ParameterHidesMemberVariable", "InnerClassFieldHidesOuterClassField" })
    static class Builder0 {

        private final ImmutableResourceCost resourceCost;

        private Builder0(final ImmutableResourceCost resourceCost) {
            this.resourceCost = resourceCost;
        }

        Builder1 buildSpeed(final int buildSpeed) {
            return new Builder1( buildSpeed );
        }

        class Builder1 {

            private final int buildSpeed;

            private Builder1(final int buildSpeed) {
                this.buildSpeed = buildSpeed;
            }

            ConstructorModule buildsModule(final ModuleType<?> buildsModule) {
                return new ConstructorModule( resourceCost, buildSpeed, buildsModule );
            }
        }
    }
}
