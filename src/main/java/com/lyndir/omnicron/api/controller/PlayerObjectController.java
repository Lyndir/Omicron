package com.lyndir.omnicron.api.controller;

import com.google.common.collect.ImmutableList;
import com.lyndir.omnicron.api.model.*;
import org.jetbrains.annotations.NotNull;


public abstract class PlayerObjectController<O extends GameObject> extends GameObjectController<O> {

    protected PlayerObjectController(final O gameObject) {

        super( gameObject );
    }

    @Override
    public Player getPlayer() {

        return getGameObject().getPlayer();
    }
}
