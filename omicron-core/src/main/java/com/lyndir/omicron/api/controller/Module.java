package com.lyndir.omicron.api.controller;

import com.google.common.base.Preconditions;
import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.system.util.MetaObject;
import com.lyndir.lhunath.opal.system.util.ObjectMeta;
import com.lyndir.omicron.api.model.*;


public abstract class Module extends MetaObject {

    @ObjectMeta(ignoreFor = ObjectMeta.For.all)
    protected final Logger logger = Logger.get( getClass() );

    private final ImmutableResourceCost resourceCost;

    private GameObject gameObject;

    protected Module(final ImmutableResourceCost resourceCost) {
        this.resourceCost = resourceCost;
        Preconditions.checkState( getType().getModuleType().isInstance( this ), "Invalid module type for module: %s", this );
    }

    public ImmutableResourceCost getResourceCost() {
        return resourceCost;
    }

    public void setGameObject(final GameObject gameObject) {

        this.gameObject = gameObject;
    }

    public GameObject getGameObject() {

        return Preconditions.checkNotNull( gameObject, "This module has not yet been initialized by its game object." );
    }

    public abstract void onReset();

    public abstract void onNewTurn();

    public abstract ModuleType<?> getType();
}
