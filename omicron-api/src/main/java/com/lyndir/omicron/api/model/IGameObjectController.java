package com.lyndir.omicron.api.model;

public interface IGameObjectController<O extends IGameObject> extends GameObserver {

    O getGameObject();
}
