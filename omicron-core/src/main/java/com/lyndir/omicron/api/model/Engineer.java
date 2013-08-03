package com.lyndir.omicron.api.model;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.lyndir.omicron.api.controller.*;
import javax.annotation.Nonnull;


public class Engineer extends PlayerObject {

    private final EngineerController controller = new EngineerController( this );

    protected Engineer(final Tile locationTile, final Player owningPlayer) {

        super( "Engineer", owningPlayer, locationTile, //
               new BaseModule( 10, 2, 3, ImmutableSet.of( LevelType.GROUND ) ), //
               new MobilityModule( 5, ImmutableMap.of( LevelType.GROUND, 1d ), ImmutableMap.<LevelType, Double>of() ) );
    }

    @Nonnull
    @Override
    public EngineerController getController() {

        return controller;
    }
}
