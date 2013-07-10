package com.lyndir.omnicron.api.model;

import com.lyndir.omnicron.api.controller.*;
import org.jetbrains.annotations.NotNull;


public class Scout extends PlayerObject {

    private final ScoutController controller = new ScoutController( this );

    public Scout(final Tile locationTile, final Player owningPlayer) {

        super( "Scout", owningPlayer, locationTile, //
               new BaseModule( 5, 3, 5, Level.set( GroundLevel.class ) ), //
               new MobilityModule( 8, Level.map( GroundLevel.class, 1 ) ), //
               new WeaponModule( 3, 3, 5, 3, 20, Level.set( GroundLevel.class ) ) );
    }

    @NotNull
    @Override
    public ScoutController getController() {

        return controller;
    }
}
