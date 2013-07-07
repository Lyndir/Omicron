package com.lyndir.omnicron.api.model;

import com.google.common.base.Preconditions;
import com.lyndir.lhunath.opal.system.util.MetaObject;
import com.lyndir.omnicron.api.controller.BaseController;
import com.lyndir.omnicron.api.controller.ObjectController;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * <i>10 15, 2012</i>
 *
 * @author lhunath
 */
public abstract class GameObject extends MetaObject implements GameObserver {

    private       Tile location;
    private final int  objectID;

    protected GameObject(final int objectID, final Tile location) {

        this.objectID = objectID;
        this.location = location;

        Preconditions.checkState( location.getContents() == null, "Cannot create object on tile that is not empty: %s", location );
        location.setContents( this );
    }

    @Nullable
    @Override
    public GameObserver getParent() {

        return getPlayer();
    }

    @NotNull
    @Override
    public abstract ObjectController<? extends GameObject> getController();

    public int getObjectID() {

        return objectID;
    }

    public Tile getLocation() {

        return location;
    }

    public void setLocation(final Tile location) {

        this.location = location;
    }
}
