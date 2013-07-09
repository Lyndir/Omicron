package com.lyndir.omnicron.api.controller;

import com.google.common.base.Preconditions;
import com.lyndir.omnicron.api.model.*;
import java.util.*;


public class MobilityModule extends Module {

    private final int movementSpeed;
    private final Map<Class<? extends Level>, Integer> levelMultipliers = new HashMap<>();

    private int remainingSpeed;

    public MobilityModule(final int movementSpeed, final Map<Class<? extends Level>, Integer> levelMultipliers) {

        this.movementSpeed = movementSpeed;
        this.levelMultipliers.putAll( levelMultipliers );
    }

    public void move(final Player currentPlayer, final int du, final int dv) {

        Preconditions.checkArgument( currentPlayer.equals( getGameObject().getPlayer() ),
                                     "Cannot move object that doesn't belong to the current player." );

        Tile currentLocation = getGameObject().getLocation();
        Coordinate newPosition = currentLocation.getPosition().delta( du, dv );
        Tile newTile = currentLocation.getLevel().getTile( newPosition );
        Preconditions.checkArgument( newTile != null, "Cannot move to new position: it is outside the level's bounds." );
        assert newTile != null;

        int distance = currentLocation.getPosition().distanceTo( newPosition );
        Preconditions.checkArgument( distance == 1, "Can only move in increments of one tile, tried to move %s tiles.", distance );

        int speedCost = distance * levelMultipliers.get( currentLocation.getLevel().getClass() );
        int newRemainingSpeed = remainingSpeed - speedCost;
        Preconditions.checkArgument( newRemainingSpeed >= 0, "Cannot move: insufficient speed remaining this turn." );

        remainingSpeed = newRemainingSpeed;
        getGameObject().getLocation().setContents( null );
        getGameObject().setLocation( newTile );
        newTile.setContents( getGameObject() );
    }

    @Override
    public void newTurn() {

        remainingSpeed = movementSpeed;
    }
}
