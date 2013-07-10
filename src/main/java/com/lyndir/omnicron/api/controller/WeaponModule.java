package com.lyndir.omnicron.api.controller;

import com.google.common.base.Preconditions;
import com.lyndir.omnicron.api.model.*;
import java.util.Set;


public class WeaponModule extends Module {

    private final int                         firePower;
    private final int                         variance;
    private final int                         range;
    private final Set<Class<? extends Level>> supportedLayers;

    public WeaponModule(final int firePower, final int variance, final int range, final Set<Class<? extends Level>> supportedLayers) {

        this.firePower = firePower;
        this.variance = variance;
        this.range = range;
        this.supportedLayers = supportedLayers;
    }

    public int getFirePower() {

        return firePower;
    }

    public int getVariance() {

        return variance;
    }

    public int getRange() {

        return range;
    }

    public Set<Class<? extends Level>> getSupportedLayers() {

        return supportedLayers;
    }

    public void fireAt(final Player currentPlayer, final GameObject target) {

        Preconditions.checkArgument( currentPlayer.equals( getGameObject().getPlayer() ), //
                                     "Cannot fire: unit is not owned by player." );
        Preconditions.checkArgument( currentPlayer.canObserve( currentPlayer, target.getLocation() ), //
                                     "Cannot fire: target not observed." );
        Preconditions.checkArgument( getGameObject().getLocation().getPosition().distanceTo( target.getLocation().getPosition() ) <= range,
                                     "Cannot fire: target not in range." );


    }

    @Override
    public void newTurn() {

    }
}
