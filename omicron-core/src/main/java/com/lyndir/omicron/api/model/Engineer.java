package com.lyndir.omicron.api.model;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.lyndir.omicron.api.controller.*;
import org.jetbrains.annotations.NotNull;


public class Engineer extends PlayerObject {

    private final EngineerController controller = new EngineerController( this );

    protected Engineer(final Tile locationTile, final Player owningPlayer) {

        super( "Engineer", owningPlayer, locationTile, //
               new BaseModule( 10, 2, 3, ImmutableSet.of( LevelType.GROUND ) ), //
               new MobilityModule( 5, ImmutableMap.of( LevelType.GROUND, 1d ), ImmutableMap.<LevelType, Double>of() ) );
    }

    @NotNull
    @Override
    public EngineerController getController() {

        return controller;
    }
}
