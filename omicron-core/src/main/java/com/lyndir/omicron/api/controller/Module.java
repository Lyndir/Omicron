package com.lyndir.omicron.api.controller;

import com.google.common.base.Preconditions;
import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.omicron.api.model.GameObject;


public abstract class Module {

    final Logger logger = Logger.get( getClass() );

    private GameObject gameObject;

    public void setGameObject(final GameObject gameObject) {

        this.gameObject = gameObject;
    }

    public GameObject getGameObject() {

        return Preconditions.checkNotNull( gameObject, "This module has not yet been initialized by its game object." );
    }

    public abstract void onNewTurn();
}
