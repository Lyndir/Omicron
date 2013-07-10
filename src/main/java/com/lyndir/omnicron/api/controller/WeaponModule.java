package com.lyndir.omnicron.api.controller;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.lyndir.omnicron.api.model.*;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class WeaponModule extends Module {

    private final int                         firePower;
    private final int                         varience;
    private final int                         range;
    private final Set<Class<? extends Level>> supportedLayers;

    public WeaponModule(final int firePower, final int varience, final int range, final Set<Class<? extends Level>> supportedLayers) {

        this.firePower = firePower;
        this.varience = varience;
        this.range = range;
        this.supportedLayers = supportedLayers;
    }

    public int getFirePower() {

        return firePower;
    }

    public int getVarience() {

        return varience;
    }

    public int getRange() {

        return range;
    }

    public Set<Class<? extends Level>> getSupportedLayers() {

        return supportedLayers;
    }

    public void fireAt(final Player currentPlayer, final GameObject target) {

        Preconditions.checkArgument( currentPlayer.equals( getGameObject().getPlayer() ), "Only the owner of this game object can fire its weapons." );
    }

    @Override
    public void newTurn() {

    }
}
