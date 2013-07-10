package com.lyndir.omicron.api.model;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.lyndir.omicron.api.controller.*;
import org.jetbrains.annotations.NotNull;


public class Scout extends PlayerObject {

    private final ScoutController controller = new ScoutController( this );

    public Scout(final Tile locationTile, final Player owningPlayer) {

        super( "Scout", owningPlayer, locationTile, //
               new BaseModule( 5, 3, 5, ImmutableSet.of( LevelType.GROUND ) ), //
               new MobilityModule( 8, ImmutableMap.of( LevelType.GROUND, 1f ), ImmutableMap.<LevelType, Float>of() ), //
               new WeaponModule( 3, 3, 5, 3, 20, ImmutableSet.of( LevelType.GROUND ) ) );
    }

    @NotNull
    @Override
    public ScoutController getController() {

        return controller;
    }
}
