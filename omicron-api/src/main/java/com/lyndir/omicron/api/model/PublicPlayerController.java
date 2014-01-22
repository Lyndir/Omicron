package com.lyndir.omicron.api.model;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableCollection;
import com.lyndir.omicron.api.Authenticated;
import com.lyndir.omicron.api.util.Maybe;
import com.lyndir.omicron.api.util.Maybool;
import javax.annotation.Nonnull;


public class PublicPlayerController implements IPlayerController {

    private final IPlayerController core;

    PublicPlayerController(final IPlayerController core) {
        this.core = core;
    }

    @Override
    public int hashCode() {
        return core.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof PublicPlayerController)
            return core.equals( ((PublicPlayerController) obj).core );

        return core.equals( obj );
    }

    @Override
    public IPlayer getPlayer() {
        return core.getPlayer();
    }

    @Nonnull
    @Override
    public Optional<? extends IPlayer> getOwner() {
        return core.getOwner();
    }

    @Override
    public IGameController getGameController() {
        return core.getGameController();
    }

    @Override
    @Authenticated
    public Maybool canObserve(@Nonnull final ITile location) {
        return core.canObserve( location );
    }

    @Nonnull
    @Override
    @Authenticated
    public Iterable<? extends ITile> listObservableTiles() {
        return core.listObservableTiles();
    }

    /**
     * List the objects of this player.
     *
     * NOTE: The controller must be of the currently authenticated player.
     *
     * @return A list of game objects owned by this controller's player.
     */
    @Override
    @Authenticated
    public ImmutableCollection<? extends IGameObject> listObjects()
            throws Security.NotAuthenticatedException {
        return core.listObjects();
    }

    /**
     * Iterate the objects of this player that the given observer (and the current player) can observe.
     *
     *
     * @param observer The observer that we want to observe the player's objects with.
     *
     * @return An iterable of game objects owned by this controller's player.
     */
    @Override
    @Authenticated
    public Iterable<? extends IGameObject> iterateObservableObjects(final GameObserver observer) {
        return core.iterateObservableObjects( observer );
    }

    @Override
    @Authenticated
    public Maybe<? extends IGameObject> getObject(final int objectId)
            throws Security.NotAuthenticatedException {
        return core.getObject( objectId );
    }
}
