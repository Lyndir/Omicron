package com.lyndir.omnicron.api.controller;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.lyndir.omnicron.api.model.*;
import java.util.Random;
import java.util.Set;


public class WeaponModule extends Module {

    private static final Random RANDOM = new Random();
    private final int                         firePower;
    private final int                         variance;
    private final int                         range;
    private final int                         repeat;
    private final int                         ammunitionLoad;
    private final Set<Class<? extends Level>> supportedLayers;
    private       int                         repeated;
    private       int                         ammunition;

    public WeaponModule(final int firePower, final int variance, final int range, final int repeat, final int ammunitionLoad, final Set<Class<? extends Level>> supportedLayers) {

        this.firePower = firePower;
        this.variance = variance;
        this.range = range;
        this.repeat = repeat;
        this.ammunitionLoad = ammunitionLoad;
        this.supportedLayers = supportedLayers;

        ammunition = ammunitionLoad;
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

    public int getRepeat() {

        return repeat;
    }

    public int getAmmunitionLoad() {

        return ammunitionLoad;
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
        Preconditions.checkState( repeated < repeat, //
                                  "Cannot fire: no repeats left." );
        Preconditions.checkState( ammunition > 0, //
                                  "Cannot fire: no ammunition left." );

        Optional<GameObject> targetGameObject = target.getContents();
        if (targetGameObject.isPresent())
            targetGameObject.get().onModule( BaseModule.class ).addDamage( firePower + RANDOM.nextInt( variance ) );

        ++repeated;
        --ammunition;
    }

    @Override
    public void newTurn() {

        repeated = 0;
    }
}
