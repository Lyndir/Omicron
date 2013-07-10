package com.lyndir.omicron.api.controller;

import com.lyndir.omicron.api.model.GameObject;


public abstract class Module {

    private GameObject gameObject;

    public void setGameObject(final GameObject gameObject) {

        this.gameObject = gameObject;
    }

    public GameObject getGameObject() {

        return gameObject;
    }

    public abstract void newTurn();
}
