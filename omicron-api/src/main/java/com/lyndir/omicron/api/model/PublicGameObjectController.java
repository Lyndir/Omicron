package com.lyndir.omicron.api.model;

import com.google.common.base.Optional;
import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.system.util.*;
import com.lyndir.omicron.api.Authenticated;
import com.lyndir.omicron.api.util.Maybool;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class PublicGameObjectController<O extends IGameObject> extends MetaObject implements IGameObjectController<O> {

    @ObjectMeta(ignoreFor = ObjectMeta.For.all)
    protected final Logger logger = Logger.get( getClass() );

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

    @Nonnull
    @Override
    public Optional<? extends IPlayer> getOwner() {
        return core.getOwner();
    }

    @Override
    @Authenticated
    public Maybool canObserve(@Nonnull final ITile location) {
        return core.canObserve( location );
    }

    @Nonnull
    @Override
    @Authenticated
    public Iterable<? extends ITile> listObservableTiles() {
        return core.listObservableTiles();
    }
}
