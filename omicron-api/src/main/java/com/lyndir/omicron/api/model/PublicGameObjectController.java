package com.lyndir.omicron.api.model;

import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.system.util.*;
import com.lyndir.omicron.api.Authenticated;
import com.lyndir.omicron.api.util.Maybool;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class PublicGameObjectController<O extends IGameObject> extends MetaObject implements IGameObjectController<O> {

    private final IGameObjectController<O> core;

    PublicGameObjectController(final IGameObjectController<O> core) {
        this.core = core;
    }

    @Override
    public int hashCode() {
        return core.hashCode();
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        if (obj instanceof PublicGameObjectController)
            return core.equals( ((PublicGameObjectController<?>) obj).core );

        return core.equals( obj );
    }

    @Override
    public O getGameObject() {
        return core.getGameObject();
    }

    @Override
    @Authenticated
    public Maybool canObserve(@Nonnull final GameObservable observable)
            throws Security.NotAuthenticatedException {
        return core.canObserve( observable );
    }

    @Nonnull
    @Override
    @Authenticated
    public Iterable<? extends ITile> iterateObservableTiles() {
        return core.iterateObservableTiles();
    }
}
