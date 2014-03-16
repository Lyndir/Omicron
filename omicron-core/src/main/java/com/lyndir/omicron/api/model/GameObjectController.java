package com.lyndir.omicron.api.model;

import static com.lyndir.omicron.api.model.Security.*;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.system.util.*;
import com.lyndir.omicron.api.Authenticated;
import com.lyndir.omicron.api.util.Maybool;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class GameObjectController<O extends GameObject> extends MetaObject implements IGameObjectController<O> {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger logger = Logger.get( GameObjectController.class );

    private final O gameObject;

    protected GameObjectController(final O gameObject) {
        this.gameObject = gameObject;
    }

    @Override
    public int hashCode() {
        return gameObject.hashCode();
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        return obj instanceof IGameObjectController && Objects.equals( gameObject, ((IGameObjectController<?>) obj).getGameObject() );
    }

    @Override
    public O getGameObject() {
        return gameObject;
    }

    void setOwner(@Nullable final Player owner) {
        Optional<Player> oldOwner = getGameObject().getOwner();
        if (oldOwner.isPresent() && ObjectUtils.isEqual( oldOwner.get(), owner ))
            // Object already owned by owner.
            return;

        if (oldOwner.isPresent())
            oldOwner.get().getController().removeObject( getGameObject() );

        getGameObject().setOwner( owner );

        Optional<Player> newOwner = getGameObject().getOwner();
        if (newOwner.isPresent())
            newOwner.get().addObjects( getGameObject() );
    }

    void setLocation(@Nonnull final Tile location) {
        Tile oldLocation = getGameObject().getLocation();
        if (ObjectUtils.isEqual( oldLocation, location ))
            // Object already at location.
            return;

        oldLocation.setContents( null );
        getGameObject().setLocation( location );
        location.setContents( getGameObject() );
    }

    /**
     * @see BaseModule#canObserve(GameObservable)
     */
    @Override
    public Maybool canObserve(@Nonnull final GameObservable observable)
            throws NotAuthenticatedException {
        return getGameObject().onModuleElse( ModuleType.BASE, 0, Maybool.NO ).canObserve( observable );
    }

    @Nonnull
    @Override
    @Authenticated
    public Iterable<Tile> iterateObservableTiles()
            throws NotAuthenticatedException, NotObservableException {
        return getGameObject().onModuleElse( ModuleType.BASE, 0, ImmutableList.of() ).iterateObservableTiles();
    }

    void onReset() {
        for (final Module module : getGameObject().listModules())
            module.onReset();
    }

    void onNewTurn() {
        for (final Module module : getGameObject().listModules())
            module.onNewTurn();
    }

    void die() {
        GameObject gameObject = getGameObject();
        gameObject.getGame().getController().fireIfObservable( gameObject ) //
                .onUnitDied( gameObject );

        // Remove from the game: player.
        Optional<Player> owner = gameObject.getOwner();
        if (owner.isPresent())
            owner.get().getController().removeObject( gameObject );

        // Remove from the game: level.
        gameObject.getLocation().setContents( null );
    }

    /**
     * Replace this controller's object with a new object in the game.  The existing object essentially "transforms" and will not die.  This
     * call registers the replacement object into the game if it hasn't been already.
     *
     * @param replacementObject The object to replace this object with.
     *
     * @return The replacement object.
     */
    void replaceWith(final GameObject replacementObject) {
        GameObject gameObject = getGameObject();

        // Remove from the game: player.
        Optional<Player> owner = gameObject.getOwner();
        if (owner.isPresent())
            owner.get().getController().removeObject( gameObject );

        // Replace in the game: level.
        gameObject.getLocation().replaceContents( replacementObject );

        replacementObject.register();
    }
}
