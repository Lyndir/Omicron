package com.lyndir.omnicron.api.model;

import com.lyndir.omnicron.api.controller.BaseController;
import org.jetbrains.annotations.NotNull;


public abstract class BaseGameObject extends GameObject {

    private final BaseModule baseModule;

    protected BaseGameObject(final Tile locationTile, final BaseModule baseModule) {

        super( baseModule.getOwningPlayer().getController().newObjectID(), locationTile );

        this.baseModule = baseModule;
    }

    @NotNull
    @Override
    public Player getPlayer() {

        return baseModule.getOwningPlayer();
    }

    @NotNull
    @Override
    public abstract BaseController<? extends BaseGameObject> getController();

    public BaseModule getBaseModule() {

        return baseModule;
    }
}
