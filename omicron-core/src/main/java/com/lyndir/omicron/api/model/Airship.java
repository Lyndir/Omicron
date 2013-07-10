package com.lyndir.omicron.api.model;

import com.lyndir.omicron.api.controller.*;
import org.jetbrains.annotations.NotNull;


public class Airship extends PlayerObject {

    private final AirshipController controller = new AirshipController( this );

    protected Airship(final Tile locationTile, final Player owningPlayer) {

        super( "Airship", owningPlayer, locationTile,
               new BaseModule( 5, 1, 5, Level.set( SkyLevel.class ) ),
               new MobilityModule(2, Level.map( SkyLevel.class, 1 ) ) );
    }

    @NotNull
    @Override
    public AirshipController getController() {

        return controller;
    }
}
