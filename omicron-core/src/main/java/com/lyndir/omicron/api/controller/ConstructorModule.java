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

package com.lyndir.omicron.api.controller;

import static com.lyndir.lhunath.opal.system.util.ObjectUtils.*;

import com.google.common.base.*;
import com.google.common.collect.*;
import com.lyndir.lhunath.opal.system.util.*;
import com.lyndir.omicron.api.model.*;
import com.lyndir.omicron.api.util.PathUtils;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class ConstructorModule extends Module {

    private static final double MAX_DISTANCE_TO_CONSTRUCTOR = 5;
    private final int           buildSpeed;
    private final ModuleType<?> buildsModule;

    private int remainingSpeed;

    public ConstructorModule(final int buildSpeed, final ModuleType<?> buildsModule) {
        this.buildSpeed = buildSpeed;
        this.buildsModule = buildsModule;
    }

    @Override
    public void onNewTurn() {
        remainingSpeed = buildSpeed;
    }

    private void construct(final ConstructionSite site) {
        int reducedComplexity = site.reduceComplexity( buildsModule, remainingSpeed );
        remainingSpeed -= reducedComplexity;
    }

    public ModuleType<?> getBuildsModule() {
        return buildsModule;
    }

    public int getBuildSpeed() {
        return buildSpeed;
    }

    public int getRemainingSpeed() {
        return remainingSpeed;
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

        return new ConstructionSite( unitType, Preconditions.checkNotNull( getGameObject().getPlayer() ), location );
    }

    /**
     * A construction site is a unit that is under construction.  Its controller manages its construction progress and it turns into the
     * constructed unit upon completion.
     */
    public static class ConstructionSite extends PlayerObject {

        private final UnitType constructionUnitType;
        private final Map<ModuleType<?>, Integer> remainingComplexity = Maps.newHashMap();

        private ConstructionSite(final UnitType constructionUnitType, final Player owner, final Tile location) {
            super( UnitTypes.CONSTRUCTION, owner, location );
            this.constructionUnitType = constructionUnitType;
            for (final Module module : constructionUnitType.createModules())
                remainingComplexity.put( module.getType(), ifNotNullElse( remainingComplexity.get( module.getType() ), 0 )
                                                           + constructionUnitType.getComplexity() );
        }

        private int getRemainingComplexity(final ModuleType<?> moduleType) {
            return ifNotNullElse( remainingComplexity.get( moduleType ), 0 );
        }

        private int reduceComplexity(final ModuleType<?> moduleType, final int reduction) {
            int remaining = getRemainingComplexity( moduleType );
            int reduced = Math.min( remaining, reduction );
            remainingComplexity.put( moduleType, remaining - reduced );

            return reduced;
        }

        @Nonnull
        @Override
        public PlayerObjectController<? extends PlayerObject> getController() {
            return new PlayerObjectController<PlayerObject>( this ) {

                @Override
                public void onNewTurn() {
                    super.onNewTurn();

                    // Initialize path finding functions.
                    PredicateNN<GameObject> foundFunction = new PredicateNN<GameObject>() {
                        @Override
                        public boolean apply(@Nonnull final GameObject input) {
                            for (final ConstructorModule constructorModule : input.getModules( ModuleType.CONSTRUCTOR ))
                                if (constructorModule.getRemainingSpeed() > 0
                                    && getRemainingComplexity( constructorModule.getBuildsModule() ) > 0)
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

                    // Find paths to constructor and use them to work on the job.
                    while (true) {
                        Optional<PathUtils.Path<GameObject>> path = PathUtils.find( getGameObject(), foundFunction, costFunction,
                                                                                    MAX_DISTANCE_TO_CONSTRUCTOR, neighboursFunction );
                        if (!path.isPresent())
                            // No more constructors with remaining speed or construction finished.
                            break;

                        for (final ConstructorModule constructorModule : path.get().getTarget().getModules( ModuleType.CONSTRUCTOR ))
                            constructorModule.construct( ConstructionSite.this );
                    }

                    // Check if we managed to resolve all the complexity.
                    if (FluentIterable.from( remainingComplexity.values() ).filter( new PredicateNN<Integer>() {
                        @Override
                        public boolean apply(@Nonnull final Integer input) {
                            return input > 0;
                        }
                    } ).isEmpty()) {
                        // No more complexity remaining; create the constructed unit.
                        die();
                        getLocation().setContents(
                                new PlayerObject( constructionUnitType, Preconditions.checkNotNull( getPlayer() ), getLocation() ) );
                    }
                }
            };
        }
    }
}
