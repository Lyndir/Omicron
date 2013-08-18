package com.lyndir.omicron.api.model;

import com.google.common.base.Optional;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.lyndir.lhunath.opal.system.util.*;
import com.lyndir.omicron.api.Constants;
import com.lyndir.omicron.api.util.PathUtils;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class ExtractorModule extends Module {

    private final ResourceType resourceType;
    private final int          speed;

    protected ExtractorModule(final ImmutableResourceCost resourceCost, final ResourceType resourceType, final int speed) {
        super( resourceCost );

        this.resourceType = resourceType;
        this.speed = speed;
    }

    static Builder0 createWithStandardResourceCost() {
        return createWithExtraResourceCost( ResourceCost.immutable() );
    }

    static Builder0 createWithExtraResourceCost(final ImmutableResourceCost resourceCost) {
        return new Builder0( ModuleType.EXTRACTOR.getStandardCost().add( resourceCost ) );
    }

    public ResourceType getResourceType() {
        assertObservable();

        return resourceType;
    }

    public int getSpeed() {
        assertObservable();

        return speed;
    }

    @Override
    protected void onReset() {
    }

    @Override
    protected void onNewTurn() {
        // Mine some resources.
        Tile location = getGameObject().getLocation();
        Optional<Integer> availableResources = location.getResourceQuantity( resourceType );
        if (!availableResources.isPresent())
            // No resources left to mine.
            return;

        int newAvailableResources = Math.max( 0, availableResources.get() - speed );
        int minedResources = availableResources.get() - newAvailableResources;
        if (!availableResources.isPresent())
            // No speed left for mining.
            return;

        // Initialize path finding functions.
        PredicateNN<GameObject> foundFunction = new PredicateNN<GameObject>() {
            @Override
            public boolean apply(@Nonnull final GameObject input) {
                for (final ContainerModule containerModule : input.getModules( ModuleType.CONTAINER ))
                    if (containerModule.getAvailable() > 0)
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

        // Find paths to containers and deposit mined resources.
        while (minedResources > 0) {
            Optional<PathUtils.Path<GameObject>> path = PathUtils.find( getGameObject(), foundFunction, costFunction,
                                                                        Constants.MAX_DISTANCE_TO_CONTAINER, neighboursFunction );
            if (!path.isPresent())
                // No more containers with available capacity.
                break;

            for (final ContainerModule containerModule : path.get().getTarget().getModules( ModuleType.CONTAINER ))
                minedResources -= containerModule.addStock( minedResources );
        }

        // If we have minedResources left that we weren't able to stock, put them back in the tile (ie. don't extract them).
        newAvailableResources += minedResources;

        // Update the amount of resources left in the tile after this turn's extraction.
        location.setResourceQuantity( resourceType, newAvailableResources );
        logger.trc( "unstocked resources: %d %s, left in tile: %d", minedResources, resourceType, newAvailableResources );
    }

    @Override
    public ModuleType<?> getType() {
        return ModuleType.EXTRACTOR;
    }

    @SuppressWarnings({ "ParameterHidesMemberVariable", "InnerClassFieldHidesOuterClassField" })
    static class Builder0 {

        private final ImmutableResourceCost resourceCost;

        private Builder0(final ImmutableResourceCost resourceCost) {
            this.resourceCost = resourceCost;
        }

        Builder1 resourceType(final ResourceType resourceType) {
            return new Builder1( resourceType );
        }

        class Builder1 {

            private final ResourceType resourceType;

            private Builder1(final ResourceType resourceType) {
                this.resourceType = resourceType;
            }

            ExtractorModule speed(final int speed) {
                return new ExtractorModule( resourceCost, resourceType, speed );
            }
        }
    }
}
