package com.lyndir.omicron.api.model;

import com.lyndir.lhunath.opal.system.util.MetaObject;


public class PublicObjectController<O extends IGameObject> extends MetaObject implements IObjectController<O> {

    private final IObjectController<O> core;

    PublicObjectController(final IObjectController<O> core) {
        this.core = core;
    }
}
