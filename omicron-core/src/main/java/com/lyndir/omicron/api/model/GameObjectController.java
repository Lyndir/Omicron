package com.lyndir.omicron.api.model;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.system.util.MetaObject;
import com.lyndir.lhunath.opal.system.util.ObjectMeta;
import javax.annotation.Nonnull;


public class GameObjectController<O extends GameObject> extends MetaObject implements GameObserver {

    @ObjectMeta(ignoreFor = ObjectMeta.For.all)
    final Logger logger = Logger.get( getClass() );

    private final O gameObject;

    protected GameObjectController(final O gameObject) {

        this.gameObject = gameObject;

        Optional<Player> owner = getOwner();
        if (owner.isPresent())
            owner.get().getController().addObject( getGameObject() );
    }

    public O getGameObject() {

        return gameObject;
    }

    @Nonnull
    @Override
    public Optional<Player> getOwner() {
        return getGameObject().getOwner();
    }

    public void setOwner(final Player owner) {
        Optional<Player> oldOwner = getOwner();
        if (oldOwner.isPresent())
            oldOwner.get().getObjects().remove( getGameObject() );

        getGameObject().setOwner( owner );

        Optional<Player> newOwner = getOwner();
        if (newOwner.isPresent())
            newOwner.get().getController().addObject( getGameObject() );
    }

    public void setLocation(final Tile location) {

        Tile oldLocation = getGameObject().getLocation();
        if (oldLocation != null)
            oldLocation.setContents( null );

        getGameObject().setLocation( location );

        Tile newLocation = getGameObject().getLocation();
        if (newLocation != null)
            newLocation.setContents( getGameObject() );
    }

    @Override
    public boolean canObserve(@Nonnull final Player currentPlayer, @Nonnull final Tile location) {

        return getGameObject().onModuleElse( ModuleType.BASE, 0, false ).canObserve( currentPlayer, location );
    }

    @Nonnull
    @Override
    public Iterable<Tile> listObservableTiles(@Nonnull final Player currentPlayer) {

        return getGameObject().onModuleElse( ModuleType.BASE, 0, ImmutableList.of() ).listObservableTiles( currentPlayer );
    }

    public void onReset() {

        for (final Module module : getGameObject().listModules())
            module.onReset();
    }

    public void onNewTurn() {

        for (final Module module : getGameObject().listModules())
            module.onNewTurn();
    }

    public void die() {

        getGameObject().getLocation().setContents( null );

        Optional<Player> owner = getOwner();
        if (owner.isPresent())
            owner.get().getObjects().remove( getGameObject() );
    }
}
