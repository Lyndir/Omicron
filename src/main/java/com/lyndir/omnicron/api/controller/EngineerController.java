package com.lyndir.omnicron.api.controller;

import com.lyndir.omnicron.api.model.*;


public class EngineerController extends GameObjectController<Engineer> {

    public EngineerController(final Engineer gameObject) {

        super( gameObject );
    }

    @Override
    public Player getPlayer() {

        return getGameObject().getPlayer();
    }
}
