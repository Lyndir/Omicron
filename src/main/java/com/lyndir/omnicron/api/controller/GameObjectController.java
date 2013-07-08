package com.lyndir.omnicron.api.controller;

import com.lyndir.omnicron.api.model.*;
import com.lyndir.omnicron.api.view.PlayerGameInfo;
import org.jetbrains.annotations.NotNull;


public abstract class GameObjectController<O extends GameObject> implements GameObserver {

    private final O gameObject;

    protected GameObjectController(final O gameObject) {

        this.gameObject = gameObject;
    }

    public O getGameObject() {

        return gameObject;
    }

    @Override
    public boolean canObserve(@NotNull final Player currentPlayer, @NotNull final Tile location) {

        return getGameObject().onModuleElse( BaseModule.class, false ).canObserve( currentPlayer, location );
    }

    public void newTurn() {

        for (final Module module : getGameObject().listModules())
            module.newTurn();
    }
}
