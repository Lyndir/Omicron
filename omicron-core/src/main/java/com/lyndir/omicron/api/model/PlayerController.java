package com.lyndir.omicron.api.model;

import static com.lyndir.omicron.api.model.Security.*;

import com.google.common.base.*;
import com.google.common.collect.*;
import com.lyndir.lhunath.opal.system.util.ObjectUtils;
import com.lyndir.omicron.api.Authenticated;
import com.lyndir.omicron.api.util.Maybe;
import com.lyndir.omicron.api.util.Maybool;
import javax.annotation.Nonnull;


public class PlayerController implements GameObserver {

    private final Player         player;
    private       GameController gameController;

    PlayerController(@Nonnull final Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    @Nonnull
    @Override
    public Optional<Player> getOwner() {
        return Optional.of( getPlayer() );
    }

    void setGameController(final GameController gameController) {
        Preconditions.checkState( this.gameController == null, "This player has already been added to a game!" );
        this.gameController = gameController;

        // Key-less players immediately set themselves ready to start the game.
        if (player.isKeyLess())
            gameController.setReady( getPlayer() );
    }

    public GameController getGameController() {
        return Preconditions.checkNotNull( gameController, "This player has not yet been added to a game!" );
    }

    @Override
    @Authenticated
    public Maybool canObserve(@Nonnull final Tile location) {
        return FluentIterable.from( getPlayer().getObjects() ).transform( new Function<GameObject, Maybool>() {
            @Override
            public Maybool apply(final GameObject input) {
                return input.canObserve( location );
            }
        } ).firstMatch( new Predicate<Maybool>() {
            @Override
            public boolean apply(final Maybool input) {
                return input.isTrue();
            }
        } ).or( Maybool.NO );
    }

    @Nonnull
    @Override
    @Authenticated
    public Iterable<Tile> listObservableTiles() {
        return FluentIterable.from( getPlayer().getObjects() ).transformAndConcat( new Function<GameObject, Iterable<? extends Tile>>() {
            @Override
            public Iterable<? extends Tile> apply(final GameObject input) {
                return input.listObservableTiles();
            }
        } );
    }

    /**
     * List the objects of this player.
     *
     * NOTE: The controller must be of the currently authenticated player.
     *
     * @return A list of game objects owned by this controller's player.
     */
    @Authenticated
    public ImmutableCollection<GameObject> listObjects() {
        if (!ObjectUtils.isEqual( getPlayer(), currentPlayer() ))
            return ImmutableSet.of();

        return ImmutableSet.copyOf( getPlayer().getObjects() );
    }

    /**
     * Iterate the objects of this player that the given observer (and the current player) can observe.
     *
     * @param observer The observer that we want to observe the player's objects with.
     *
     * @return An iterable of game objects owned by this controller's player.
     */
    @Authenticated
    public Iterable<GameObject> iterateObservableObjects(final GameObserver observer) {
        return FluentIterable.from( getPlayer().getObjects() ).filter( new Predicate<GameObject>() {
            @Override
            public boolean apply(final GameObject input) {
                return observer.canObserve( input.getLocation() ).isTrue();
            }
        } );
    }

    @Authenticated
    public Maybe<GameObject> getObject(final int objectId) {
        Optional<GameObject> object = getPlayer().getObject( objectId );

        if (ObjectUtils.isEqual( currentPlayer(), getPlayer() ))
            if (object.isPresent())
                return Maybe.of( object.get() );
            else
                return Maybe.absent();

        if (object.isPresent())
            if (currentPlayer().canObserve( object.get().getLocation() ).isTrue())
                return Maybe.of( object.get() );
            else
                return Maybe.unknown();

        return Maybe.unknown();
    }

    int newObjectID() {
        return getPlayer().nextObjectID();
    }

    void removeObject(final GameObject gameObject) {
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
