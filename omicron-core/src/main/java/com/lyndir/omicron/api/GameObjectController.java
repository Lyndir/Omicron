package com.lyndir.omicron.api;

import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.system.util.*;
import com.lyndir.omicron.api.util.Maybe;
import java.util.Objects;
import java.util.Optional;
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
        if (oldOwner.isPresent() && oldOwner.get().equals( owner ))
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
        Maybe<Tile> oldLocation = getGameObject().getLocation();
        if (oldLocation.isPresent() && oldLocation.get().equals( location ))
            // Object already at location.
            return;

        oldLocation.get().setContents( null );
        getGameObject().setLocation( location );
        location.setContents( getGameObject() );
    }

    void onReset() {
        for (final IModule module : getGameObject().getModules())
            Module.cast( module ).onReset();
    }

    void onNewTurn() {
        for (final IModule module : getGameObject().getModules())
            Module.cast( module ).onNewTurn();
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
        gameObject.getLocation().get().setContents( null );
    }

    /**
     * Replace this controller's object with a new object in the game.  The existing object essentially "transforms" and will not die.
     * This
     * call registers the replacement object into the game if it hasn't been already.
     *
     * @param replacementObject The object to replace this object with.
     */
    void replaceWith(final GameObject replacementObject) {
        GameObject gameObject = getGameObject();

        // Remove from the game: player.
        Optional<Player> owner = gameObject.getOwner();
        if (owner.isPresent())
            owner.get().getController().removeObject( gameObject );

        // Replace in the game: level.
        gameObject.getLocation().get().replaceContents( replacementObject );

        replacementObject.register();
    }
}
