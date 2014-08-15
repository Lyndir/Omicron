package com.lyndir.omicron.api;

import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.system.util.*;
import com.lyndir.omicron.api.util.Maybe;
import com.lyndir.omicron.api.util.PathUtils;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;


public class ExtractorModule extends Module implements IExtractorModule, IExtractorModuleController {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger logger = Logger.get( ExtractorModule.class );

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

    @Override
    public ResourceType getResourceType() {
        assertObservable();

        return resourceType;
    }

    @Override
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
        Tile location = getGameObject().getLocation().get();
        Maybe<Integer> availableResources = location.getResourceQuantity( resourceType );
        if (!availableResources.isPresent())
            // No resources left to mine.
            return;

        int newAvailableResources = Math.max( 0, availableResources.get() - speed );
        int minedResources = availableResources.get() - newAvailableResources;
        if (!availableResources.isPresent())
            // No speed left for mining.
            return;

        // Initialize path finding functions.
        PredicateNN<IGameObject> foundFunction = gameObject -> {
            for (final ContainerModule containerModule : gameObject.getModules( ModuleType.CONTAINER ))
                if (containerModule.getAvailable() > 0)
                    return true;

            return false;
        };
        NNFunctionNN<PathUtils.Step<IGameObject>, Double> costFunction = gameObjectStep -> 1d;
        NNFunctionNN<IGameObject, Stream<? extends IGameObject>> neighboursFunction = neighbour -> //
                neighbour.getLocation().get().neighbours().stream() //
                        .map( new Function<ITile, IGameObject>() {
                            @Override
                            public IGameObject apply(final ITile tile) {
                                return tile.getContents().orNull();
                            }
                        } ).filter( gameObject -> gameObject != null );

        // Find paths to containers and deposit mined resources.
        while (minedResources > 0) {
            Optional<PathUtils.Path<IGameObject>> path = PathUtils.find( getGameObject(), foundFunction, costFunction,
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
    public IExtractorModuleController getController() {
        return this;
    }

    @Override
    public IExtractorModule getModule() {
        return this;
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
