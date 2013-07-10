package com.lyndir.omicron.api.model;

import com.lyndir.omicron.api.controller.*;
import org.jetbrains.annotations.NotNull;


public class Engineer extends PlayerObject {

    private final EngineerController controller = new EngineerController( this );

    protected Engineer(final Tile locationTile, final Player owningPlayer) {

        super( "Engineer", owningPlayer, locationTile, //
               new BaseModule( 10, 2, 3, Level.set( GroundLevel.class ) ), //
               new MobilityModule( 5, Level.map( GroundLevel.class, 1 ) ) );
    }

    @NotNull
    @Override
    public EngineerController getController() {

        return controller;
    }
}
