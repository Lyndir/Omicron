package com.lyndir.omicron.api.controller;

import com.google.common.collect.ImmutableList;
import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.system.util.MetaObject;
import com.lyndir.lhunath.opal.system.util.ObjectMeta;
import com.lyndir.omicron.api.model.*;
import javax.annotation.Nonnull;


public abstract class GameObjectController<O extends GameObject> extends MetaObject implements GameObserver {

    @ObjectMeta(ignoreFor = ObjectMeta.For.all)
    final Logger logger = Logger.get( getClass() );

    private final O gameObject;

    protected GameObjectController(final O gameObject) {

        this.gameObject = gameObject;
    }

    public O getGameObject() {

        return gameObject;
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

    public void onNewTurn() {

        for (final Module module : getGameObject().listModules())
            module.onNewTurn();
    }

    public void die() {

        getGameObject().getLocation().setContents( null );

        Player player = getPlayer();
        if (player != null)
            player.getObjects().remove( getGameObject() );
    }
}
