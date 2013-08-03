package com.lyndir.omicron.api.controller;

import com.lyndir.omicron.api.model.*;


public class PlayerObjectController<O extends GameObject> extends GameObjectController<O> {

    public PlayerObjectController(final O gameObject) {

        super( gameObject );
    }

    @Override
    public Player getPlayer() {

        return getGameObject().getPlayer();
    }
}
