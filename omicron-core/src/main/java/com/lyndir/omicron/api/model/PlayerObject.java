package com.lyndir.omicron.api.model;

import com.lyndir.omicron.api.controller.*;
import javax.annotation.Nonnull;


/**
 * <i>10 15, 2012</i>
 *
 * @author lhunath
 */
public class PlayerObject extends GameObject {

    private final Player owner;

    public PlayerObject(final UnitType unitType, final Player owner, final Tile location) {
        super(unitType, owner.nextObjectID(), location );

        this.owner = owner;
    }

    @Nonnull
    @Override
    public PlayerObjectController<? extends PlayerObject> getController() {
        return new PlayerObjectController<>(this);
    }

    @Nonnull
    @Override
    public final Player getPlayer() {

        return owner;
    }
}
