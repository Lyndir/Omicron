package com.lyndir.omicron.api.controller;

import com.google.common.base.*;
import com.google.common.collect.FluentIterable;
import com.lyndir.omicron.api.model.*;
import org.jetbrains.annotations.NotNull;


public class PlayerController implements GameObserver {

    private final Player         player;
    private       GameController gameController;

    public PlayerController(final Player player) {

        this.player = player;
    }

    @Override
    public Player getPlayer() {

        return player;
    }

    public void setGameController(final GameController gameController) {
        Preconditions.checkState( this.gameController == null, "This player has already been added to a game!" );
        this.gameController = gameController;
    }

    public GameController getGameController() {
        return Preconditions.checkNotNull( gameController, "This player has not yet been added to a game!" );
    }

    @Override
    public boolean canObserve(@NotNull final Player currentPlayer, @NotNull final Tile location) {

        return FluentIterable.from( player.getObjects() ).anyMatch( new Predicate<GameObject>() {
            @Override
            public boolean apply(final GameObject input) {

                return input.canObserve( currentPlayer, location );
            }
        } );
    }

    @NotNull
    @Override
    public Iterable<Tile> listObservableTiles(@NotNull final Player currentPlayer) {

        return FluentIterable.from( iterateObservableObjects( currentPlayer ) )
                             .transformAndConcat( new Function<GameObject, Iterable<? extends Tile>>() {
                                 @Override
                                 public Iterable<? extends Tile> apply(final GameObject input) {

                                     return input.listObservableTiles( currentPlayer );
                                 }
                             } );
    }

    public Iterable<GameObject> iterateObservableObjects(final GameObserver observer) {

        return FluentIterable.from( player.getObjects() ).filter( new Predicate<GameObject>() {
            @Override
            public boolean apply(final GameObject input) {

                Player player = observer.getPlayer();
                if (player == null)
                    return observer.equals( input );

                return observer.canObserve( player, input.getLocation() );
            }
        } );
    }

    public Optional<GameObject> getObject(final Player currentPlayer, final int objectId) {

        GameObject object = player.getObject( objectId );

        // If the object cannot be observed by the current player, treat it as absent.
        if (object != null && !currentPlayer.canObserve( currentPlayer, object.getLocation() ))
            return Optional.absent();

        return Optional.fromNullable( object );
    }

    public int newObjectID() {

        return player.nextObjectID();
    }

    public void addObject(final GameObject gameObject) {

        Preconditions.checkState( gameObject.getPlayer() == player, "Cannot add object to this player: belongs to another player." );
        player.addObject( gameObject );
    }

    public void onNewTurn(final GameController gameController) {

        for (final GameObject gameObject : player.getObjects())
            gameObject.getController().newTurn();

        if (player.isKeyLess())
            gameController.setReady( player );
    }
}
