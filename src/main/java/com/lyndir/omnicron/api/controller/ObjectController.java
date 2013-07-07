package com.lyndir.omnicron.api.controller;

import com.lyndir.omnicron.api.model.*;


public class ObjectController<O extends GameObject> implements GameObserverController {

    private final O gameObject;

    public ObjectController(final O gameObject) {

        this.gameObject = gameObject;
    }

    protected O getGameObject() {

        return gameObject;
    }

    @Override
    public boolean canObserve(final Player currentPlayer, final Tile tile) {

        if (gameObject.getPlayer() != currentPlayer)
            return false;

        return gameObject.getLocation().getPosition().equals( tile.getPosition() );
    }

    public void move(final Player currentPlayer, final int du, final int dv) {

        Size levelSize = gameObject.getLocation().getLevel().getLevelSize();
        Coordinate newPosition = gameObject.getLocation().getPosition().delta( du, dv, levelSize );
        Tile newTile = gameObject.getLocation().getLevel().getTile( newPosition );
        if (newTile == null)
            throw new IllegalArgumentException( "No tile at: " + newPosition );

        gameObject.setLocation( newTile );
    }
}
