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

import static com.lyndir.lhunath.opal.system.util.ObjectUtils.*;

import com.google.common.base.*;
import com.google.common.collect.*;
import com.lyndir.lhunath.opal.system.util.*;
import com.lyndir.omicron.api.Constants;
import com.lyndir.omicron.api.util.PathUtils;
import java.util.*;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class ConstructorModule extends Module {

    private final int           buildSpeed;
    private final ModuleType<?> buildsModule;

    private boolean    resourceConstrained;
    private int        remainingSpeed;
    private GameObject target;

    protected ConstructorModule(final ImmutableResourceCost resourceCost, final int buildSpeed, final ModuleType<?> buildsModule) {
        super( resourceCost );

        this.buildSpeed = buildSpeed;
        this.buildsModule = buildsModule;
    }

    public static Builder0 createWithStandardResourceCost() {
        return createWithExtraResourceCost( ImmutableResourceCost.immutable() );
    }

    public static Builder0 createWithExtraResourceCost(final ImmutableResourceCost resourceCost) {
        return new Builder0( ModuleType.CONSTRUCTOR.getStandardCost().add( resourceCost ) );
    }

    @Override
    public void onReset() {
        resourceConstrained = false;
        remainingSpeed = buildSpeed;
    }

    @Override
    public void onNewTurn() {
    }

    // This method assumes a target link between this module and the site exists.
    private void construct(final ConstructionSite site) {
        if (isResourceConstrained())
            return;

        for (; remainingSpeed > 0; --remainingSpeed) {
            /* Find resource cost */
            Optional<ImmutableResourceCost> resourceCostOptional = site.getResourceCostToPerformWork( getBuildsModule() );
            if (!resourceCostOptional.isPresent())
                // No work left to do.
                return;
            final MutableResourceCost resourceCost = ResourceCost.mutable( resourceCostOptional.get() );

            /* Find resource stock to cover cost */
            // Initialize path finding functions.
            PredicateNN<GameObject> foundFunction = new PredicateNN<GameObject>() {
                @Override
                public boolean apply(@Nonnull final GameObject input) {
                    for (final ContainerModule containerModule : input.getModules( ModuleType.CONTAINER ))
                        if (resourceCost.get( containerModule.getResourceType() ) > 0 && containerModule.getStock() > 0)
                            return true;

                    return false;
                }
            };
            NNFunctionNN<PathUtils.Step<GameObject>, Double> costFunction = new NNFunctionNN<PathUtils.Step<GameObject>, Double>() {
                @Nonnull
                @Override
                public Double apply(@Nonnull final PathUtils.Step<GameObject> input) {
                    return 1d;
                }
            };
            NNFunctionNN<GameObject, Iterable<GameObject>> neighboursFunction = new NNFunctionNN<GameObject, Iterable<GameObject>>() {
                @Nonnull
                @Override
                public Iterable<GameObject> apply(@Nonnull final GameObject input) {
                    return FluentIterable.from( input.getLocation().neighbours() ).transform( new NFunctionNN<Tile, GameObject>() {
                        @Nullable
                        @Override
                        public GameObject apply(@Nonnull final Tile input) {
                            return input.getContents().orNull();
                        }
                    } ).filter( Predicates.notNull() );
                }
            };

            /* Find paths to containers and deposit mined resources. */
            ImmutableMap.Builder<ContainerModule, Integer> borrowedResources = ImmutableMap.builder();
            while (!resourceCost.isZero()) {
                Optional<PathUtils.Path<GameObject>> path = PathUtils.find( getGameObject(), foundFunction, costFunction,
                                                                            Constants.MAX_DISTANCE_TO_CONTAINER, neighboursFunction );
                if (!path.isPresent()) {
                    resourceConstrained = true;
                    // No more containers with available stock: not enough resources available to complete work unit.
                    // Give borrowed resources back to containers.
                    for (final Map.Entry<ContainerModule, Integer> borrowEntry : borrowedResources.build().entrySet())
                        borrowEntry.getKey().addStock( borrowEntry.getValue() );

                    return;
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
    }

    public ModuleType<?> getBuildsModule() {
        return buildsModule;
    }

    public int getBuildSpeed() {
        return buildSpeed;
    }

    public boolean isResourceConstrained() {
        return resourceConstrained;
    }

    public int getRemainingSpeed() {
        return remainingSpeed;
    }

    public GameObject getTarget() {
        return target;
    }

    public void setTarget(final GameObject target) {
        Preconditions.checkArgument( ObjectUtils.isEqual( getGameObject().getOwner(), target.getOwner() ),
                                     "Can only target units of the same player." );
        this.target = target;
    }

    @Override
    public ModuleType<?> getType() {
        return ModuleType.CONSTRUCTOR;
    }

    public Set<? extends UnitType> blueprints() {
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
    public ConstructionSite schedule(final UnitType unitType, final Tile location) {
        Preconditions.checkArgument( location.isAccessible() );
        Preconditions.checkArgument( location.getLevel().equals( getGameObject().getLocation().getLevel() ) );
        Preconditions.checkArgument( location.getPosition().distanceTo( getGameObject().getLocation().getPosition() ) == 1 );

        ConstructionSite site = new ConstructionSite( unitType, getGameObject().getGame(), getGameObject().getOwner().get(), location );
        setTarget( site );

        return site;
    }

    /**
     * A construction site is a unit that is under construction.  Its controller manages its construction progress and it turns into the
     * constructed unit upon completion.
     */
    public static class ConstructionSite extends GameObject {

        private final UnitType constructionUnitType;
        private final Map<ModuleType<?>, Integer> remainingWork = Maps.newHashMap();
        private final List<? extends Module> constructionModules;

        private ConstructionSite(@Nonnull final UnitType constructionUnitType, @Nonnull final Game game, @Nonnull final Player owner,
                                 @Nonnull final Tile location) {
            super( UnitTypes.CONSTRUCTION, game, owner, location );

            this.constructionUnitType = constructionUnitType;
            constructionModules = constructionUnitType.createModules();

            for (final Module module : constructionModules)
                remainingWork.put( module.getType(),
                                   ifNotNullElse( remainingWork.get( module.getType() ), 0 ) + constructionUnitType.getConstructionWork() );
        }

        public int getRemainingWork(final ModuleType<?> moduleType) {
            return ifNotNullElse( remainingWork.get( moduleType ), 0 );
        }

        public ImmutableResourceCost getRemainingResourceCost() {
            MutableResourceCost remainingResourceCost = ResourceCost.mutable();
            for (final Module constructionModule : constructionModules)
                remainingResourceCost.add(
                        constructionModule.getResourceCost().multiply( getRemainingWork( constructionModule.getType() ) ) );

            return ResourceCost.immutable( remainingResourceCost );
        }

        public Optional<ImmutableResourceCost> getResourceCostToPerformWork(final ModuleType<?> moduleType) {
            for (final Module constructionModule : constructionModules)
                if (constructionModule.getType().equals( moduleType ) && getRemainingWork( constructionModule.getType() ) > 0)
                    return Optional.of( constructionModule.getResourceCost() );

            return Optional.absent();
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
                remainingWork.put( moduleType, --remaining );
                return true;
            }

            return false;
        }

        @Nonnull
        @Override
        public GameObjectController<? extends GameObject> getController() {
            return new GameObjectController<GameObject>( this ) {

                @Override
                public void onNewTurn() {
                    super.onNewTurn();

                    // Initialize path finding functions.
                    PredicateNN<GameObject> foundFunction = new PredicateNN<GameObject>() {
                        @Override
                        public boolean apply(@Nonnull final GameObject input) {
                            for (final ConstructorModule module : input.getModules( ModuleType.CONSTRUCTOR ))
                                if (module.getRemainingSpeed() > 0 && !module.isResourceConstrained()
                                    && getRemainingWork( module.getBuildsModule() ) > 0)
                                    return true;

                            return false;
                        }
                    };
                    NNFunctionNN<PathUtils.Step<GameObject>, Double> costFunction = new NNFunctionNN<PathUtils.Step<GameObject>, Double>() {
                        @Nonnull
                        @Override
                        public Double apply(@Nonnull final PathUtils.Step<GameObject> input) {
                            return 1d;
                        }
                    };
                    NNFunctionNN<GameObject, Iterable<GameObject>> neighboursFunction = new NNFunctionNN<GameObject, Iterable<GameObject>>() {
                        @Nonnull
                        @Override
                        public Iterable<GameObject> apply(
                                @SuppressWarnings("ParameterNameDiffersFromOverriddenParameter") @Nonnull final GameObject neighbourInput) {
                            return FluentIterable.from( neighbourInput.getLocation().neighbours() )
                                                 .transform( new NFunctionNN<Tile, GameObject>() {
                                                     @Nullable
                                                     @Override
                                                     public GameObject apply(@Nonnull final Tile input) {
                                                         Optional<GameObject> contents = input.getContents();
                                                         if (contents.isPresent()) {
                                                             for (final ConstructorModule module : contents.get()
                                                                                                           .getModules(
                                                                                                                   ModuleType.CONSTRUCTOR )) {
                                                                 if (ObjectUtils.isEqual( module.getTarget(), neighbourInput )) {
                                                                     return contents.get();
                                                                 }
                                                             }
                                                         }

                                                         return null;
                                                     }
                                                 } )
                                                 .filter( Predicates.notNull() );
                        }
                    };

                    // Find paths to constructor and use them to work on the job.
                    while (true) {
                        Optional<PathUtils.Path<GameObject>> path = PathUtils.find( getGameObject(), foundFunction, costFunction,
                                                                                    Constants.MAX_DISTANCE_TO_CONSTRUCTOR,
                                                                                    neighboursFunction );
                        if (!path.isPresent())
                            // No more constructors with remaining speed or construction finished.
                            break;

                        for (final ConstructorModule constructorModule : path.get().getTarget().getModules( ModuleType.CONSTRUCTOR ))
                            constructorModule.construct( ConstructionSite.this );
                    }

                    // Check if we managed to complete all the work.
                    if (FluentIterable.from( remainingWork.values() ).filter( new PredicateNN<Integer>() {
                        @Override
                        public boolean apply(@Nonnull final Integer input) {
                            return input > 0;
                        }
                    } ).isEmpty()) {
                        // No more work remaining; create the constructed unit.
                        die();
                        new GameObject( constructionUnitType, getGame(), getOwner().get(), getLocation() );
                    }
                }
            };
        }
    }


    @SuppressWarnings({ "ParameterHidesMemberVariable", "InnerClassFieldHidesOuterClassField" })
    public static class Builder0 {

        private final ImmutableResourceCost resourceCost;

        private Builder0(final ImmutableResourceCost resourceCost) {
            this.resourceCost = resourceCost;
        }

        public Builder1 buildSpeed(final int buildSpeed) {
            return new Builder1( buildSpeed );
        }

        public class Builder1 {

            private final int buildSpeed;

            private Builder1(final int buildSpeed) {
                this.buildSpeed = buildSpeed;
            }

            public ConstructorModule supportedLayers(final ModuleType<?> buildsModule) {
                return new ConstructorModule( resourceCost, buildSpeed, buildsModule );
            }
        }
    }
}
