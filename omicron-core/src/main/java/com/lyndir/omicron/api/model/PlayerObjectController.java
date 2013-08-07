package com.lyndir.omicron.api.model;

import javax.annotation.Nonnull;


public class PlayerObjectController<O extends PlayerObject> extends GameObjectController<O> {

    public PlayerObjectController(final O gameObject) {
        super( gameObject );

        getPlayer().getController().addObject( getGameObject() );
    }

    @Override
    @Nonnull
    public Player getPlayer() {

        return getGameObject().getPlayer();
    }
}
