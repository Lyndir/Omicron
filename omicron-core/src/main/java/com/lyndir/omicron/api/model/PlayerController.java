package com.lyndir.omicron.api.model;

import com.google.common.base.*;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.lyndir.lhunath.opal.system.util.ObjectUtils;
import javax.annotation.Nonnull;


public class PlayerController implements GameObserver {

    private final Player         player;
    private       GameController gameController;

    PlayerController(@Nonnull final Player player) {

        this.player = player;
    }

    @Nonnull
    @Override
    public Optional<Player> getOwner() {

        return Optional.of( player );
    }

    void setGameController(final GameController gameController) {
        Preconditions.checkState( this.gameController == null, "This player has already been added to a game!" );
        this.gameController = gameController;
    }

    public GameController getGameController() {
        return Preconditions.checkNotNull( gameController, "This player has not yet been added to a game!" );
    }

    @Override
    public boolean canObserve(@Nonnull final Player currentPlayer, @Nonnull final Tile location) {

        return FluentIterable.from( player.getObjects() ).anyMatch( new Predicate<GameObject>() {
            @Override
            public boolean apply(final GameObject input) {

                return input.canObserve( currentPlayer, location );
            }
        } );
    }

    @Nonnull
    @Override
    public Iterable<Tile> listObservableTiles(@Nonnull final Player currentPlayer) {

        return FluentIterable.from( player.getObjects() ).transformAndConcat( new Function<GameObject, Iterable<? extends Tile>>() {
            @Override
            public Iterable<? extends Tile> apply(final GameObject input) {

                return input.listObservableTiles( currentPlayer );
            }
        } );
    }

    /**
     * Iterate the objects of this player that the given observer (and the current player) can observe.
     *
     * @param currentPlayer The player that's making the request.
     * @param observer      The observer that we want to observe the player's objects with.
     *
     * @return An iterable of game objects owned by this controller's player.
     */
    public Iterable<GameObject> iterateObservableObjects(final Player currentPlayer, final GameObserver observer) {

        return FluentIterable.from( player.getObjects() ).filter( new Predicate<GameObject>() {
            @Override
            public boolean apply(final GameObject input) {

                if (!currentPlayer.canObserve( currentPlayer, input.getLocation() ))
                    return false;

                return observer.canObserve( currentPlayer, input.getLocation() );
            }
        } );
    }

    public Optional<GameObject> getObject(final Player currentPlayer, final int objectId) {

        Optional<GameObject> object = player.getObject( objectId );

        // If the object cannot be observed by the current player, treat it as absent.
        if (object.isPresent() && !currentPlayer.canObserve( currentPlayer, object.get().getLocation() ))
            return Optional.absent();

        return object;
    }

    int newObjectID() {

        return player.nextObjectID();
    }

    void addObject(final GameObject gameObject) {

        Preconditions.checkState( ObjectUtils.isEqual( player, gameObject.getOwner().orNull() ),
                                  "Cannot add object to this player: belongs to another player." );
        player.addObject( gameObject );
    }

    protected void onReset() {

        for (final GameObject gameObject : ImmutableList.copyOf( player.getObjects() ))
            gameObject.getController().onReset();
    }

    protected void onNewTurn() {

        for (final GameObject gameObject : ImmutableList.copyOf( player.getObjects() ))
            gameObject.getController().onNewTurn();

        if (player.isKeyLess())
            gameController.setReady( player );
    }
}
