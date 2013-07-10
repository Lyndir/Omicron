package com.lyndir.omicron.api.model;

import com.google.common.collect.ImmutableSet;
import com.google.inject.internal.ImmutableMap;
import com.lyndir.omicron.api.controller.*;
import org.jetbrains.annotations.NotNull;


public class Airship extends PlayerObject {

    private final AirshipController controller = new AirshipController( this );

    protected Airship(final Tile locationTile, final Player owningPlayer) {

        super( "Airship", owningPlayer, locationTile, //
               new BaseModule( 5, 1, 5, ImmutableSet.of( LevelType.SKY ) ), //
               new MobilityModule( 2, ImmutableMap.of( LevelType.SKY, 1f ), ImmutableMap.<LevelType, Float>of() ) );
    }

    @NotNull
    @Override
    public AirshipController getController() {

        return controller;
    }
}
