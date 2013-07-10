package com.lyndir.omnicron.api.controller;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.lyndir.omnicron.api.model.*;
import java.util.Random;
import java.util.Set;


public class WeaponModule extends Module {

    private final static Random RANDOM = new Random();
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

    public void fireAt(final Player currentPlayer, final Tile target) {

        Preconditions.checkArgument( currentPlayer.equals( getGameObject().getPlayer() ), //
                                     "Cannot fire: unit is not owned by player." );
        Preconditions.checkArgument( currentPlayer.canObserve( currentPlayer, target ), //
                                     "Cannot fire: target not observed." );
        Preconditions.checkArgument( getGameObject().getLocation().getPosition().distanceTo( target.getPosition() ) <= range, //
                                     "Cannot fire: target not in range." );

        Optional<GameObject> targetGameObject = target.getContents();
        if (targetGameObject.isPresent())
            targetGameObject.get().onModule( BaseModule.class ).addDamage( firePower + RANDOM.nextInt( variance ) );
    }

    @Override
    public void newTurn() {

    }
}
