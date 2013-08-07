package com.lyndir.omicron.api.model;

import javax.annotation.Nonnull;


/**
 * <i>10 15, 2012</i>
 *
 * @author lhunath
 */
public class PlayerObject extends GameObject {

    private final Player owner;

    public PlayerObject(@Nonnull final UnitType unitType, @Nonnull final Game game, @Nonnull final Player owner, @Nonnull final Tile location) {
        super(unitType, game, owner.nextObjectID(), location );

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
