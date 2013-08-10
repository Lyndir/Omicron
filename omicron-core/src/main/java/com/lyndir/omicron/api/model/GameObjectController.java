package com.lyndir.omicron.api.model;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.system.util.MetaObject;
import com.lyndir.lhunath.opal.system.util.ObjectMeta;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class GameObjectController<O extends GameObject> extends MetaObject implements GameObserver {

    @ObjectMeta(ignoreFor = ObjectMeta.For.all)
    final Logger logger = Logger.get( getClass() );

    private final O gameObject;

    protected GameObjectController(final O gameObject) {

        this.gameObject = gameObject;

        // Register ourselves into the game.
        setOwner( getGameObject().getOwner().orNull() );
        setLocation( getGameObject().getLocation() );
    }

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
        if (oldOwner.isPresent())
            oldOwner.get().getObjects().remove( getGameObject() );

        getGameObject().setOwner( owner );

        Optional<Player> newOwner = getOwner();
        if (newOwner.isPresent())
            newOwner.get().getController().addObject( getGameObject() );
    }

    void setLocation(final Tile location) {

        Tile oldLocation = getGameObject().getLocation();
        if (oldLocation != null)
            oldLocation.setContents( null );

        getGameObject().setLocation( location );

        Tile newLocation = getGameObject().getLocation();
        if (newLocation != null)
            newLocation.setContents( getGameObject() );
    }

    @Override
    public boolean canObserve(@Nonnull final Tile location) {

        return getGameObject().onModuleElse( ModuleType.BASE, 0, false ).canObserve( location );
    }

    @Nonnull
    @Override
    public Iterable<Tile> listObservableTiles() {

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

        Optional<Player> owner = getOwner();
        if (owner.isPresent())
            owner.get().getObjects().remove( getGameObject() );
    }
}
