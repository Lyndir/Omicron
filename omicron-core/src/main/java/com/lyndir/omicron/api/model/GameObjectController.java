package com.lyndir.omicron.api.model;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.system.util.*;
import com.lyndir.omicron.api.Authenticated;
import com.lyndir.omicron.api.util.Maybool;
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
            newOwner.get().addObject( getGameObject() );
    }

    void setLocation(@Nonnull final Tile location) {
        final Tile oldLocation = getGameObject().getLocation();
        if (oldLocation != null && ObjectUtils.isEqual( oldLocation, location ))
            // Object already at location.
            return;

        if (oldLocation != null)
            oldLocation.setContents( null );

        getGameObject().setLocation( location );

        final Tile newLocation = getGameObject().getLocation();
        if (newLocation != null)
            newLocation.setContents( getGameObject() );
    }

    @Override
    @Authenticated
    public Maybool canObserve(@Nonnull final ITile location)
            throws Security.NotAuthenticatedException, Security.NotObservableException {
        if (getGameObject().isOwnedByCurrentPlayer() && ObjectUtils.equals( location, getGameObject().getLocation() ))
            return Maybool.YES;

        return getGameObject().onModuleElse( ModuleType.BASE, 0, Maybool.NO ).canObserve( location );
    }

    @Nonnull
    @Override
    @Authenticated
    public Iterable<Tile> listObservableTiles()
            throws Security.NotAuthenticatedException, Security.NotObservableException {
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
        getGameObject().getLocation().setContents( null );
        setOwner( null );

        getGameObject().getGame().getController().fireIfObservable( getGameObject().getLocation() ) //
                .onUnitDied( getGameObject() );
    }
}
