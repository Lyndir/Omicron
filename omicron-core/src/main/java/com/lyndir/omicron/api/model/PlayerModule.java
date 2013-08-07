package com.lyndir.omicron.api.model;

import com.google.common.base.Preconditions;


public abstract class PlayerModule extends Module {

    private PlayerObject gameObject;

    protected PlayerModule(final ImmutableResourceCost resourceCost) {
        super( resourceCost );
    }

    public void setGameObject(final PlayerObject gameObject) {

        this.gameObject = gameObject;
    }

    @Override
    public PlayerObject getGameObject() {

        return Preconditions.checkNotNull( gameObject, "This module has not yet been initialized by its game object." );
    }
}
