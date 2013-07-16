package com.lyndir.omicron.api.controller;

import static com.lyndir.lhunath.opal.system.util.ObjectUtils.*;

import com.google.common.base.Optional;
import com.lyndir.omicron.api.model.*;
import java.util.*;


public class MobilityModule extends Module {

    private final int movementSpeed;
    private final Map<LevelType, Float> movementCost = new EnumMap<>( LevelType.class );
    private final Map<LevelType, Float> levelingCost = new EnumMap<>( LevelType.class );

    private float remainingSpeed;

    public MobilityModule(final int movementSpeed, final Map<LevelType, Float> movementCost, final Map<LevelType, Float> levelingCost) {

        this.movementSpeed = movementSpeed;
        this.movementCost.putAll( movementCost );
        this.levelingCost.putAll( levelingCost );
    }

    /**
     * Get the speed cost related to moving around in the given level.
     *
     * @param levelType The level to move around in.
     *
     * @return The speed cost.
     */
    public float costForMovingInLevel(final LevelType levelType) {

        return ifNotNullElse( movementCost.get( levelType ), Float.MAX_VALUE );
    }

    /**
     * Get the speed cost related to leveling from the current level type to the given level type.
     *
     * @param levelType The level to transition to.
     *
     * @return The speed cost.
     */
    public float costForLevelingToLevel(final LevelType levelType) {

        LevelType currentLevel = getGameObject().getLocation().getLevel().getType();
        if (levelType == currentLevel)
            return 0;

        // Level up until we reach the target level.
        float cost = 0;
        do {
            Optional<LevelType> newLevel = currentLevel.up();
            if (!newLevel.isPresent())
                break;

            currentLevel = newLevel.get();
            cost += levelingCost.get( currentLevel );

            if (currentLevel == levelType)
                return cost;
        }
        while (true);

        // Level down until we reach the target level.
        cost = 0;
        do {
            Optional<LevelType> newLevel = currentLevel.down();
            if (!newLevel.isPresent())
                break;

            currentLevel = newLevel.get();
            cost += levelingCost.get( currentLevel );

            if (currentLevel == levelType)
                return cost;
        }
        while (true);

        // Unreachable code.
        throw new IllegalArgumentException( "Unsupported level type: " + levelType );
    }

    /**
     * Move the unit to an adjacent tile.
     *
     * @param currentPlayer The player ordering the action.
     * @param side          The side of the adjacent tile relative to the current.
     */
    public boolean move(final Player currentPlayer, final Coordinate.Side side) {

        if (!currentPlayer.equals( getGameObject().getPlayer() ))
            // Cannot move object that doesn't belong to the current player.
            return false;

        Tile currentLocation = getGameObject().getLocation();
        float cost = costForMovingInLevel( currentLocation.getLevel().getType() );

        Coordinate newPosition = side.delta( currentLocation.getPosition() );
        Tile newLocation = currentLocation.getLevel().getTile( newPosition ).get();
        if (newLocation.equals( currentLocation ))
            // Already in the destination location.
            return true;

        if (!newLocation.isAccessible())
            // Cannot move: new location is not accessible.
            return false;

        float newRemainingSpeed = remainingSpeed - cost;
        if (newRemainingSpeed < 0)
            // Cannot move: insufficient speed remaining this turn.
            return false;

        remainingSpeed = newRemainingSpeed;
        getGameObject().getLocation().setContents( null );
        getGameObject().setLocation( newLocation );
        newLocation.setContents( getGameObject() );

        return true;
    }

    /**
     * Move the unit to an adjacent tile.
     *
     * @param currentPlayer The player ordering the action.
     * @param target        The side of the adjacent tile relative to the current.
     */
    public boolean move(final Player currentPlayer, final Tile target) {

        if (!currentPlayer.equals( getGameObject().getPlayer() ))
            // Cannot move object that doesn't belong to the current player.
            return false;

        if (!level( currentPlayer, target.getLevel().getType() ))
            return false;

        // Initialize cost calculation.
        Tile currentLocation = getGameObject().getLocation();
        float stepCost = costForMovingInLevel( currentLocation.getLevel().getType() );
        float currentCost = 0;

        // Initialize breath-first.
        Set<Tile> tested = new HashSet<>();
        Deque<Tile> toTest = new LinkedList<>();
        toTest.addLast( currentLocation );
        tested.add( currentLocation );

        // Search breath-first.
        while (!toTest.isEmpty()) {
            // Check cost for moving to a neighbour tile.
            currentCost += stepCost;
            float currentRemainingSpeed = remainingSpeed - currentCost;
            if (currentRemainingSpeed < 0)
                // Cannot move: insufficient speed remaining this turn.
                return false;

            // Check each neighbour tile.
            Tile testTile = toTest.removeFirst();
            for (final Coordinate.Side side : Coordinate.Side.values()) {
                Tile neighbour = testTile.neighbour( side );
                if (tested.contains( neighbour ))
                    continue;
                boolean accessible = neighbour.isAccessible();

                // Did we reach the target?
                if (neighbour.equals( target )) {
                    if (!accessible)
                        return false;

                    remainingSpeed = currentRemainingSpeed;
                    getGameObject().getLocation().setContents( null );
                    getGameObject().setLocation( neighbour );
                    neighbour.setContents( getGameObject() );

                    return true;
                }

                // Neighbour is not the target, add it for testing its neighbours later.
                if (accessible)
                    toTest.add( neighbour );
                tested.add( neighbour );
            }
        }

        return false;
    }

    /**
     * Move the unit to the given level.
     *
     * @param currentPlayer The player ordering the action.
     * @param levelType     The side of the adjacent tile relative to the current.
     */
    public boolean level(final Player currentPlayer, final LevelType levelType) {

        if (levelType == getGameObject().getLocation().getLevel().getType())
            // Already in the destination level.
            return true;

        if (!currentPlayer.equals( getGameObject().getPlayer() ))
            // Cannot level object that doesn't belong to the current player.
            return false;

        Tile currentLocation = getGameObject().getLocation();
        float cost = costForLevelingToLevel( levelType );

        Tile newLocation = currentLocation.getLevel().getGame().getLevel( levelType ).getTile( currentLocation.getPosition() ).get();
        float newRemainingSpeed = remainingSpeed - cost;
        if (newRemainingSpeed < 0)
            // Cannot move: insufficient speed remaining this turn.
            return false;

        remainingSpeed = newRemainingSpeed;
        getGameObject().getLocation().setContents( null );
        getGameObject().setLocation( newLocation );
        newLocation.setContents( getGameObject() );

        return true;
    }

    @Override
    public void onNewTurn() {

        remainingSpeed = movementSpeed;
    }
}
