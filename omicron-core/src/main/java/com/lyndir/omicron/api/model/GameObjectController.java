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

    @ObjectMeta(ignoreFor = ObjectMeta.For.all)
    final Logger logger = Logger.get( getClass() );

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

    @Nonnull
    @Override
    public Optional<Player> getOwner() {
        return getGameObject().getOwner();
    }

    void setOwner(@Nullable final Player owner) {
        Optional<Player> oldOwner = getOwner();
        if (oldOwner.isPresent() && ObjectUtils.isEqual( oldOwner.get(), owner ))
            // Object already owned by owner.
            return;

        if (oldOwner.isPresent())
            oldOwner.get().getController().removeObject( getGameObject() );

        getGameObject().setOwner( owner );

        Optional<Player> newOwner = getOwner();
        if (newOwner.isPresent())
            newOwner.get().addObjects( getGameObject() );
    }

    void setLocation(@Nonnull final Tile location) {
        Tile oldLocation = getGameObject().getLocation();
        if (oldLocation != null && ObjectUtils.isEqual( oldLocation, location ))
            // Object already at location.
            return;

        if (oldLocation != null)
            oldLocation.setContents( null );

        getGameObject().setLocation( location );

        Tile newLocation = getGameObject().getLocation();
        if (newLocation != null)
            newLocation.setContents( getGameObject() );
    }

    @Override
    @Authenticated
    public Maybool canObserve(@Nonnull final ITile location)
            throws NotAuthenticatedException, NotObservableException {
        if (isGod() || (getGameObject().isOwnedByCurrentPlayer() && ObjectUtils.equals( location, getGameObject().getLocation() )))
            return Maybool.YES;

        return getGameObject().onModuleElse( ModuleType.BASE, 0, Maybool.NO ).canObserve( location );
    }

    @Nonnull
    @Override
    @Authenticated
    public Iterable<Tile> listObservableTiles()
            throws NotAuthenticatedException, NotObservableException {
        return getGameObject().onModuleElse( ModuleType.BASE, 0, ImmutableList.of() ).listObservableTiles();
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
        gameObject.getGame().getController().fireIfObservable( gameObject.getLocation() ) //
                .onUnitDied( gameObject );

        // Remove from the game (map & player).
        Optional<Player> owner = gameObject.getOwner();
        if (owner.isPresent())
            owner.get().getController().removeObject( gameObject );
        gameObject.getLocation().setContents( null );
    }

    void replaceWith(final GameObject replacementObject) {
        GameObject currentGameObject = getGameObject();

        // Remove from the game (map & player).
        Optional<Player> owner = currentGameObject.getOwner();
        if (owner.isPresent())
            owner.get().getController().removeObject( currentGameObject );
        currentGameObject.getLocation().setContents( replacementObject );
    }
}
