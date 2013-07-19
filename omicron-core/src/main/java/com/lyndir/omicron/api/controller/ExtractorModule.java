package com.lyndir.omicron.api.controller;

import com.google.common.base.Optional;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.lyndir.lhunath.opal.system.util.*;
import com.lyndir.omicron.api.model.*;
import com.lyndir.omicron.api.util.PathUtils;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class ExtractorModule extends Module {

    private static final double MAX_DISTANCE_TO_CONTAINER = 10;

    private final ResourceType resourceType;
    private final int          speed;

    public ExtractorModule(final ResourceType resourceType, final int speed) {

        this.resourceType = resourceType;
        this.speed = speed;
    }

    public ResourceType getResourceType() {

        return resourceType;
    }

    public int getSpeed() {

        return speed;
    }

    @Override
    public void onNewTurn() {

        // Mine some resources.
        Tile location = getGameObject().getLocation();
        int availableResources = location.getResourceQuantity( resourceType );
        int newAvailableResources = Math.max( 0, availableResources - speed );
        int minedResources = availableResources - newAvailableResources;
        logger.trc( "mined: %d %s", minedResources, resourceType );
        if (minedResources == 0)
            // No resources left to mine.
            return;

        // Initialize path finding functions.
        PredicateNN<GameObject> foundFunction = new PredicateNN<GameObject>() {
            @Override
            public boolean apply(@Nonnull final GameObject input) {

                Optional<ContainerModule> container = input.getModule( ContainerModule.class );
                return container.isPresent() && container.get().getAvailable() > 0;
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
                                                                        MAX_DISTANCE_TO_CONTAINER, neighboursFunction );
            if (!path.isPresent()) {
                // No more containers with available capacity.
                logger.trc( "no more available containers." );
                break;
            }

            int stockedResources = path.get().getTarget().getModule( ContainerModule.class ).get().addStock( minedResources );
            minedResources -= stockedResources;
            logger.trc( "stocked: %d %s (remaining unstocked: %d)", stockedResources, resourceType, minedResources );
        }

        // If we have minedResources left that we weren't able to stock, put them back in the tile (ie. don't extract them).
        newAvailableResources += minedResources;

        // Update the amount of resources left in the tile after this turn's extraction.
        location.setResourceQuantity( resourceType, newAvailableResources );
        logger.trc( "unstocked resources: %d %s, left in tile: %d", minedResources, resourceType, newAvailableResources );
    }
}
