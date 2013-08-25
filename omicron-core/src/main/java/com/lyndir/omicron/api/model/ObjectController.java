package com.lyndir.omicron.api.model;

public class ObjectController<O extends IGameObject> {

    private final O gameObject;

    ObjectController(final O gameObject) {
        this.gameObject = gameObject;
    }

    protected O getGameObject() {
        return gameObject;
    }
}
