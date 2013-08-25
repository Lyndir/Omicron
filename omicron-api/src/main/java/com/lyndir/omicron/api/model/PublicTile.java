package com.lyndir.omicron.api.model;

import com.google.common.collect.ImmutableCollection;
import com.lyndir.lhunath.opal.system.util.MetaObject;
import com.lyndir.omicron.api.Authenticated;
import com.lyndir.omicron.api.util.Maybe;
import javax.annotation.Nonnull;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
public class PublicTile extends MetaObject implements ITile {

    private final ITile core;

    PublicTile(final ITile core) {
        this.core = core;
    }

    @Override
    @Nonnull
    @Authenticated
    public Maybe<? extends IGameObject> checkContents()
            throws Security.NotAuthenticatedException {
        return core.checkContents();
    }

    @Override
    public Coordinate getPosition() {
        return core.getPosition();
    }

    @Override
    public ILevel getLevel() {
        return core.getLevel();
    }

    @Override
    @Authenticated
    public Maybe<Integer> checkResourceQuantity(final ResourceType resourceType)
            throws Security.NotAuthenticatedException {
        return core.checkResourceQuantity( resourceType );
    }

    @Override
    @Nonnull
    public ITile neighbour(final Coordinate.Side side) {
        return core.neighbour( side );
    }

    @Override
    public ImmutableCollection<? extends ITile> neighbours() {
        return core.neighbours();
    }

    @Override
    public ImmutableCollection<? extends ITile> neighbours(final int distance) {
        return core.neighbours( distance );
    }

    @Override
    @Authenticated
    public Maybe<Boolean> checkContains(@Nonnull final IGameObject target)
            throws Security.NotAuthenticatedException {
        return core.checkContains( target );
    }

    /**
     * @return true if this tile is visible to the current player and has no contents.
     */
    @Override
    @Authenticated
    public boolean checkAccessible()
            throws Security.NotAuthenticatedException {
        return core.checkAccessible();
    }
}
