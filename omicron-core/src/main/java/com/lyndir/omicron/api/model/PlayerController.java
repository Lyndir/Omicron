package com.lyndir.omicron.api.model;

import static com.lyndir.omicron.api.model.Security.*;

import com.google.common.base.*;
import com.google.common.collect.*;
import com.lyndir.lhunath.opal.system.util.*;
import com.lyndir.omicron.api.Authenticated;
import com.lyndir.omicron.api.util.Maybe;
import com.lyndir.omicron.api.util.Maybool;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class PlayerController extends MetaObject implements IPlayerController {

    private final Player         player;
    private       GameController gameController;

    PlayerController(@Nonnull final Player player) {
        this.player = player;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    void setGameController(final GameController gameController) {
        Preconditions.checkState( this.gameController == null, "This player has already been added to a game!" );
        this.gameController = gameController;

        // Key-less players immediately set themselves ready to start the game.
        if (player.isKeyLess())
            gameController.setReady( getPlayer() );
    }

    @Override
    public GameController getGameController() {
        return Preconditions.checkNotNull( gameController, "This player has not yet been added to a game!" );
    }

    /**
     * @see #iterateObservableObjects()
     * @see IGameObject#canObserve(GameObservable)
     */
    @Override
    public Maybool canObserve(@Nonnull final GameObservable observable)
            throws NotAuthenticatedException {

        Iterable<GameObject> objects = iterateObservableObjects();
        if (observable instanceof GameObject && Iterables.contains( objects, observable ))
            return Maybool.YES;
        if (observable instanceof Tile && FluentIterable.from( objects ).transform( new NFunctionNN<GameObject, Tile>() {
            @Nullable
            @Override
            public Tile apply(@Nonnull final GameObject gameObject) {
                return gameObject.checkLocation().orNull();
            }
        } ).contains( observable ))
            return Maybool.YES;

        // Observable is not owned by us, check if any of our objects can see it.
        return FluentIterable.from( objects ).transform( new Function<IGameObject, Maybool>() {
            @Override
            public Maybool apply(final IGameObject gameObject) {
                return gameObject.canObserve( observable );
            }
        } ).firstMatch( new Predicate<Maybool>() {
            @Override
            public boolean apply(final Maybool result) {
                return result.isTrue();
            }
        } ).or( Maybool.NO );
    }

    /**
     * @see #iterateObservableObjects()
     * @see IGameObject#iterateObservableTiles()
     */
    @Nonnull
    @Override
    @Authenticated
    public Iterable<Tile> iterateObservableTiles()
            throws NotAuthenticatedException {
        return FluentIterable.from( iterateObservableObjects() ).transformAndConcat( new Function<GameObject, Iterable<? extends Tile>>() {
            @Override
            public Iterable<Tile> apply(final GameObject gameObject) {
                return gameObject.iterateObservableTiles();
            }
        } );
    }

    /**
     * @see IPlayer#canObserve(GameObservable)
     */
    @Override
    @Authenticated
    public Iterable<GameObject> iterateObservableObjects()
            throws NotAuthenticatedException {
        if (isGod() || getPlayer().isCurrentPlayer())
            return getPlayer().getObjects();

        return iterateObservableObjects( currentPlayer() );
    }

    /**
     * Iterate the objects of this player that the given observer (and the current player) can observe.
     *
     * @param observer The observer that we want to observe the player's objects with.
     *
     * @return An iterable of game objects owned by this controller's player.
     */
    @Override
    @Authenticated
    public Iterable<GameObject> iterateObservableObjects(final GameObserver observer) {
        return FluentIterable.from( getPlayer().getObjects() ).filter( new Predicate<GameObject>() {
            @Override
            public boolean apply(final GameObject gameObject) {
                return observer.canObserve( gameObject ).isTrue();
            }
        } );
    }

    @Override
    @Authenticated
    public Maybe<GameObject> getObject(final int objectId)
            throws NotAuthenticatedException {
        Optional<GameObject> object = getPlayer().getObject( objectId );

        if (isGod() || getPlayer().isCurrentPlayer())
            if (object.isPresent())
                return Maybe.of( object.get() );
            else
                return Maybe.absent();

        if (object.isPresent())
            if (currentPlayer().canObserve( object.get() ).isTrue())
                return Maybe.of( object.get() );
            else
                return Maybe.unknown();

        return Maybe.unknown();
    }

    int newObjectID() {
        return getPlayer().nextObjectID();
    }

    void removeObject(final IGameObject gameObject) {
        getPlayer().removeObject( gameObject );
    }

    protected void onReset() {
        for (final GameObject gameObject : ImmutableList.copyOf( getPlayer().getObjects() ))
            gameObject.getController().onReset();
    }

    protected void onNewTurn() {
        for (final GameObject gameObject : ImmutableList.copyOf( getPlayer().getObjects() ))
            gameObject.getController().onNewTurn();

        if (getPlayer().isKeyLess())
            gameController.setReady( getPlayer() );
    }

    void fireReset() {
        onReset();
    }

    void fireNewTurn() {
        onNewTurn();
    }
}
