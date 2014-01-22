package com.lyndir.omicron.api.model;

import com.lyndir.lhunath.opal.system.util.MetaObject;
import javax.annotation.Nullable;


public class PublicObjectController<O extends IGameObject> extends MetaObject implements IObjectController<O> {

    private final IObjectController<O> core;

    PublicObjectController(final IObjectController<O> core) {
        this.core = core;
    }

    @Override
    public int hashCode() {
        return core.hashCode();
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        if (obj instanceof PublicObjectController)
            return core.equals( ((PublicObjectController<?>) obj).core );

        return core.equals( obj );
    }
}
