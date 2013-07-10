package com.lyndir.omicron.api.controller;

import com.lyndir.omicron.api.model.*;


public abstract class PlayerObjectController<O extends GameObject> extends GameObjectController<O> {

    protected PlayerObjectController(final O gameObject) {

        super( gameObject );
    }

    @Override
    public Player getPlayer() {

        return getGameObject().getPlayer();
    }
}
