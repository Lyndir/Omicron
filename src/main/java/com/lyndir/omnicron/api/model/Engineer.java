package com.lyndir.omnicron.api.model;

import com.google.common.collect.ImmutableSet;
import com.lyndir.omnicron.api.controller.EngineerController;
import org.jetbrains.annotations.NotNull;


public class Engineer extends BaseGameObject {

    private final EngineerController controller = new EngineerController( this );

    protected Engineer(final Tile locationTile, final Player owningPlayer) {

        super( locationTile, new BaseModule( 10, 2, 3, ImmutableSet.<Class<? extends Level>>of( GroundLevel.class ), owningPlayer ) );
    }

    @NotNull
    @Override
    public EngineerController getController() {

        return controller;
    }
}
