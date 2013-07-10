package com.lyndir.omicron.api.controller;

import static com.lyndir.lhunath.opal.system.util.ObjectUtils.*;

import com.google.common.base.Preconditions;
import com.lyndir.omicron.api.model.*;
import java.util.EnumMap;
import java.util.Map;


public class MobilityModule extends Module {

    private final int movementSpeed;
    private final Map<LevelType, Float> speedMultipliers = new EnumMap<>( LevelType.class );

    private int remainingSpeed;

    public MobilityModule(final int movementSpeed, final Map<LevelType, Float> speedMultipliers) {

        this.movementSpeed = movementSpeed;
        this.speedMultipliers.putAll( speedMultipliers );
    }

    /**
     * Get the speed multiplier this unit experiences for moving in the given level.
     *
     * @param levelType The level to get a multiplier for.
     *
     * @return The level-specific speed factor.
     */
    public float multiplierForLevel(final LevelType levelType) {

        return ifNotNullElse( speedMultipliers.get( levelType ), 0f );
    }

    /**
     * Move the unit to an adjacent tile.
     *
     * @param currentPlayer The player ordering the action.
     * @param side          The side of the adjacent tile relative to the current.
     */
    public void move(final Player currentPlayer, final Coordinate.Side side) {

        Preconditions.checkArgument( currentPlayer.equals( getGameObject().getPlayer() ),
                                     "Cannot move object that doesn't belong to the current player." );

        Tile currentLocation = getGameObject().getLocation();
        Float speedMultiplier = multiplierForLevel( currentLocation.getLevel().getType() );
        Preconditions.checkArgument( speedMultiplier != 0, "Cannot move: unit is not mobile in this level." );

        Coordinate newPosition = side.delta( currentLocation.getPosition() );
        Tile newTile = currentLocation.getLevel().getTile( newPosition ).get();

        int distance = currentLocation.getPosition().distanceTo( newPosition );
        Preconditions.checkArgument( distance == 1, "Can only move in increments of one tile, tried to move %s tiles.", distance );

        int speedCost = (int) Math.floor( distance / speedMultiplier );
        int newRemainingSpeed = remainingSpeed - speedCost;
        Preconditions.checkArgument( newRemainingSpeed >= 0, "Cannot move: insufficient speed remaining this turn." );

        remainingSpeed = newRemainingSpeed;
        getGameObject().getLocation().setContents( null );
        getGameObject().setLocation( newTile );
        newTile.setContents( getGameObject() );
    }

    @Override
    public void onNewTurn() {

        remainingSpeed = movementSpeed;
    }
}
