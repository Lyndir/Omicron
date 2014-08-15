package com.lyndir.omicron.api;

import static com.lyndir.omicron.api.Security.*;

import com.lyndir.omicron.api.error.NotAuthenticatedException;
import java.util.Optional;
import com.google.common.base.*;
import com.google.common.collect.*;
import com.lyndir.lhunath.opal.system.util.*;
import com.lyndir.omicron.api.util.Maybe;
import com.lyndir.omicron.api.util.Maybool;
import java.util.stream.Stream;
import javax.annotation.Nonnull;


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
     * @see #playerObjectsObservable()
     * @see IGameObject#getLocation()
     * @see IGameObject#canObserve(GameObservable)
     */
    @Override
    public Maybool canObserve(@Nonnull final GameObservable observable)
            throws NotAuthenticatedException {

        if (observable instanceof GameObject && playerObjectsObservable().anyMatch( object -> object == observable ))
            return Maybool.yes();
        if (observable instanceof Tile && playerObjectsObservable().map( GameObject::getLocation ).anyMatch( tile -> tile == observable ))
            return Maybool.yes();

        // Observable is not owned by us, check if any of our objects can see it.
        return playerObjectsObservable().map( gameObject -> gameObject.canObserve( observable ) )
                                        .filter( Maybool::isTrue )
                                        .findFirst()
                                        .orElse( Maybool.no() );
    }

    /**
     * @see #playerObjectsObservable()
     * @see IGameObject#observableTiles()
     */
    @Nonnull
    @Override
    public Stream<? extends ITile> observableTiles()
            throws NotAuthenticatedException {
        return playerObjectsObservable().flatMap( IGameObject::observableTiles );
    }

    /**
     * @see #playerObjectsObservableBy(GameObserver)
     */
    @Override
    public Stream<GameObject> playerObjectsObservable()
            throws NotAuthenticatedException {
        if (isGod() || getPlayer().isCurrentPlayer())
            return getPlayer().getObjects().stream();

        return playerObjectsObservableBy( currentPlayer() );
    }

    /**
     * @see GameObserver#canObserve(GameObservable)
     */
    @Override
    public Stream<GameObject> playerObjectsObservableBy(final GameObserver observer) {
        return getPlayer().getObjects().stream().filter( gameObject -> observer.canObserve( gameObject ).isTrue() );
    }

    @Override
    public Maybe<GameObject> getObject(final int objectId)
            throws NotAuthenticatedException {
        Optional<GameObject> object = getPlayer().getObject( objectId );

        if (isGod() || getPlayer().isCurrentPlayer())
            if (object.isPresent())
                return Maybe.of( object.get() );
            else
                return Maybe.empty();

        if (object.isPresent())
            if (currentPlayer().canObserve( object.get() ).isTrue())
                return Maybe.of( object.get() );
            else
                return Maybe.unknown();

        return Maybe.unknown();
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
