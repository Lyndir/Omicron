package com.lyndir.omicron.api.core;

import static com.lyndir.lhunath.opal.system.util.ObjectUtils.isEqual;

import com.google.common.base.Optional;
import com.google.common.collect.*;
import com.lyndir.lhunath.opal.math.Side;
import com.lyndir.lhunath.opal.math.Vec2;
import com.lyndir.lhunath.opal.system.error.AlreadyCheckedException;
import com.lyndir.omicron.api.util.Maybe;
import com.lyndir.omicron.api.util.Maybool;
import javax.annotation.Nonnull;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
public interface ITile extends GameObservable {

    /**
     * @return The object currently on this tile, if one.
     */
    Maybe<? extends IGameObject> getContents();

    /**
     * @return The position of a tile within its level among the other tiles.
     */
    Vec2 getPosition();

    /**
     * @return The level that hosts this tile.
     */
    ILevel getLevel();

    /**
     * @param resourceType The resource tile to query availability for.
     *
     * @return The amount of resources still available on this tile of the given resource type.
     */
    default Maybe<Integer> getResourceQuantity(final ResourceType resourceType) {
        Maybe<ImmutableMap<ResourceType, Integer>> quantitiesByResourceType = getQuantitiesByResourceType();
        switch (quantitiesByResourceType.presence()) {
            case ABSENT:
                return Maybe.absent();
            case UNKNOWN:
                return Maybe.unknown();
            case PRESENT:
                return Maybe.fromNullable( quantitiesByResourceType.get().get( resourceType ) );
        }

        throw new AlreadyCheckedException();
    }

    /**
     * @return The quantities of the remaining resources available on this tile mapped by their resource type.
     */
    Maybe<ImmutableMap<ResourceType, Integer>> getQuantitiesByResourceType();

    /**
     * @param side The side of this tile to find the neighbour tile at.
     *
     * @return Find the tile in this tile's level adjacent to this tile on the given side.
     */
    default Optional<? extends ITile> neighbour(final Side side) {
        return getLevel().getTile( getPosition().translate( side.getDelta() ) );
    }

    /**
     * @return Find all the tiles adjacent to this tile.
     */
    default ImmutableCollection<? extends ITile> neighbours() {
        ImmutableList.Builder<? extends ITile> neighbours = ImmutableList.builder();
        for (final Side side : Side.values()) {
            Optional<? extends ITile> neighbour = neighbour( side );
            if (neighbour.isPresent())
                neighbours.add( neighbour.get() );
        }

        return neighbours.build();
    }

    /**
     * @param distance The maximum distance from this tile of the neighbouring tiles to return.
     *
     * @return Find all the tiles near this tile at a maximum given distance.
     */
    default ImmutableCollection<? extends ITile> neighbours(final int distance) {
        ImmutableList.Builder<? extends ITile> neighbours = ImmutableList.builder();
        // FIXME: Not correct.
        for (int dx = -distance; dx <= distance; ++dx)
            for (int dy = Math.max( -distance, -dx - distance ); dy <= Math.min( distance, -dx + distance ); ++dy)
                neighbours.add( getLevel().getTile( getPosition().translate( dx, dy ) ).get() );

        return neighbours.build();
    }

    /**
     * @param target The game object to check the tile for.
     *
     * @return Determine whether the given game object is on this tile.
     */
    @SuppressWarnings("NonBooleanMethodNameMayNotStartWithQuestion")
    default Maybool contains(final IGameObject target) {
        Maybe<? extends IGameObject> contents = getContents();
        switch (contents.presence()) {
            case ABSENT:
                return Maybool.NO;
            case UNKNOWN:
                return Maybool.UNKNOWN;
            case PRESENT:
                return Maybool.from( isEqual( contents.get(), target ) );
        }

        throw new AlreadyCheckedException();
    }

    /**
     * @return true if this tile is visible to the current player and has no contents.
     */
    @SuppressWarnings("NonBooleanMethodNameMayNotStartWithQuestion")
    default Maybool isAccessible() {
        Maybe<? extends IGameObject> contents = getContents();
        switch (contents.presence()) {
            case ABSENT:
                return Maybool.YES;
            case UNKNOWN:
                return Maybool.UNKNOWN;
            case PRESENT:
                return Maybool.NO;
        }

        throw new AlreadyCheckedException();
    }
}
