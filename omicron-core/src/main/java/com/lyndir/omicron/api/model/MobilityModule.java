package com.lyndir.omicron.api.model;

import static com.lyndir.lhunath.opal.system.util.ObjectUtils.*;
import static com.lyndir.omicron.api.util.PathUtils.*;

import com.google.common.base.Optional;
import com.lyndir.lhunath.opal.system.util.*;
import java.util.EnumMap;
import java.util.Map;
import javax.annotation.Nonnull;


public class MobilityModule extends PlayerModule {

    private final int movementSpeed;
    private final Map<LevelType, Double> movementCost = new EnumMap<>( LevelType.class );
    private final Map<LevelType, Double> levelingCost = new EnumMap<>( LevelType.class );

    private double remainingSpeed;

    protected MobilityModule(final ImmutableResourceCost resourceCost, final int movementSpeed, final Map<LevelType, Double> movementCost,
                             final Map<LevelType, Double> levelingCost) {
        super( resourceCost );

        this.movementSpeed = movementSpeed;
        this.movementCost.putAll( movementCost );
        this.levelingCost.putAll( levelingCost );
    }

    public static Builder0 createWithStandardResourceCost() {
        return createWithExtraResourceCost( ResourceCost.immutable() );
    }

    public static Builder0 createWithExtraResourceCost(final ImmutableResourceCost resourceCost) {
        return new Builder0( ModuleType.MOBILITY.getStandardCost().add( resourceCost ) );
    }

    /**
     * Get the speed cost related to moving around in the given level.
     *
     * @param levelType The level to move around in.
     *
     * @return The speed cost.
     */
    public double costForMovingInLevel(final LevelType levelType) {

        return ifNotNullElse( movementCost.get( levelType ), Double.MAX_VALUE );
    }

    /**
     * Get the speed cost related to leveling from the current level type to the given level type.
     *
     * @param levelType The level to transition to.
     *
     * @return The speed cost.
     */
    public double costForLevelingToLevel(final LevelType levelType) {

        LevelType currentLevel = getGameObject().getLocation().getLevel().getType();
        if (levelType == currentLevel)
            return 0;

        // Level up until we reach the target level.
        double cost = 0;
        do {
            Optional<LevelType> newLevel = currentLevel.up();
            if (!newLevel.isPresent())
                break;

            currentLevel = newLevel.get();

            Double currentLevelCost = levelingCost.get( currentLevel );
            if (currentLevelCost == null)
                // Cannot level to this level.
                return Double.MAX_VALUE;

            cost += currentLevelCost;

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

            Double currentLevelCost = levelingCost.get( currentLevel );
            if (currentLevelCost == null)
                // Cannot level to this level.
                return Double.MAX_VALUE;

            cost += currentLevelCost;

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
     * @param target        The side of the adjacent tile relative to the current.
     */
    public Movement movement(final Player currentPlayer, final Tile target) {

        if (!currentPlayer.equals( getGameObject().getPlayer() ))
            // Cannot move object that doesn't belong to the current player.
            return new Movement();

        Leveling leveling = leveling( currentPlayer, target.getLevel().getType() );
        if (!leveling.isPossible())
            // Cannot move because we can't level to the target's level.
            return new Movement();

        // Initialize cost calculation.
        Tile currentLocation = getGameObject().getLocation();
        final double stepCost = costForMovingInLevel( currentLocation.getLevel().getType() );

        // Initialize path finding data functions.
        PredicateNN<Tile> foundFunction = new PredicateNN<Tile>() {
            @Override
            public boolean apply(@Nonnull final Tile input) {

                return ObjectUtils.isEqual( input, target );
            }
        };
        NNFunctionNN<Step<Tile>, Double> costFunction = new NNFunctionNN<Step<Tile>, Double>() {
            @Nonnull
            @Override
            public Double apply(@Nonnull final Step<Tile> input) {

                if (!input.getTo().isAccessible())
                    return Double.MAX_VALUE;

                return stepCost;
            }
        };
        NNFunctionNN<Tile, Iterable<Tile>> neighboursFunction = new NNFunctionNN<Tile, Iterable<Tile>>() {
            @Nonnull
            @Override
            public Iterable<Tile> apply(@Nonnull final Tile input) {

                return input.neighbours();
            }
        };

        // Find the path!
        return new Movement( leveling, find( currentLocation, foundFunction, costFunction, remainingSpeed, neighboursFunction ) );
    }

    /**
     * Move the unit to the given level.
     *
     * @param currentPlayer The player ordering the action.
     * @param levelType     The side of the adjacent tile relative to the current.
     */
    public Leveling leveling(final Player currentPlayer, final LevelType levelType) {

        if (levelType == getGameObject().getLocation().getLevel().getType())
            // Already in the destination level.
            return new Leveling( false, levelType, 0 );

        if (!currentPlayer.equals( getGameObject().getPlayer() ))
            // Cannot level object that doesn't belong to the current player.
            return new Leveling( false, levelType, 0 );

        double cost = costForLevelingToLevel( levelType );
        if (cost > remainingSpeed)
            // Cannot move: insufficient speed remaining this turn.
            return new Leveling( false, levelType, cost );

        return new Leveling( true, levelType, cost );
    }

    @Override
    public void onReset() {
        remainingSpeed = movementSpeed;
    }

    @Override
    public void onNewTurn() {
    }

    @Override
    public ModuleType<?> getType() {
        return ModuleType.MOBILITY;
    }

    public class Leveling {

        private final boolean   possible;
        private final LevelType levelType;
        private final double    cost;

        Leveling(final boolean possible, @Nonnull final LevelType levelType, final double cost) {
            this.possible = possible;
            this.levelType = levelType;
            this.cost = cost;
        }

        public boolean isPossible() {
            return possible;
        }

        public LevelType getLevelType() {
            return levelType;
        }

        public double getCost() {
            return cost;
        }

        public boolean execute() {
            if (!isPossible())
                return false;

            Tile newLocation = getGameObject().getGame().getLevel( levelType ).getTile( getGameObject().getLocation().getPosition() ).get();

            remainingSpeed -= cost;
            getGameObject().getController().setLocation( newLocation );

            return true;
        }
    }


    public class Movement {

        private final Leveling             leveling;
        private final Optional<Path<Tile>> path;

        Movement() {
            leveling = null;
            path = Optional.absent();
        }

        Movement(@Nonnull final Leveling leveling, final Optional<Path<Tile>> path) {
            this.leveling = leveling;
            this.path = path;
        }

        public Optional<Path<Tile>> getPath() {
            return path;
        }

        public boolean isPossible() {
            return path.isPresent();
        }

        public boolean execute() {
            // Check that this movement was deemed possible.
            if (!isPossible() || leveling == null)
                return false;

            // Check that we still have sufficient remaining speed.
            if (path.get().getCost() > remainingSpeed)
                return false;

            // Check that the path can still be walked.
            Path<Tile> tracePath = path.get();
            do {
                if (!tracePath.getTarget().isAccessible())
                    // Path can no longer be walked.
                    return false;
                Optional<Path<Tile>> parent = tracePath.getParent();
                if (!parent.isPresent())
                    break;

                tracePath = parent.get();
            }
            while (true);

            // Perform the leveling.
            if (!leveling.execute())
                return false;

            // Execute the path.
            remainingSpeed -= path.get().getCost();
            getGameObject().getLocation().setContents( null );
            getGameObject().setLocation( path.get().getTarget() );
            path.get().getTarget().setContents( getGameObject() );

            return true;
        }
    }


    @SuppressWarnings({ "ParameterHidesMemberVariable", "InnerClassFieldHidesOuterClassField" })
    public static class Builder0 {

        private final ImmutableResourceCost resourceCost;

        private Builder0(final ImmutableResourceCost resourceCost) {

            this.resourceCost = resourceCost;
        }

        public Builder1 movementSpeed(final int movementSpeed) {
            return new Builder1( movementSpeed );
        }

        public class Builder1 {

            private final int movementSpeed;

            private Builder1(final int movementSpeed) {
                this.movementSpeed = movementSpeed;
            }

            public Builder2 movementCost(final Map<LevelType, Double> movementCost) {
                return new Builder2( movementCost );
            }

            public class Builder2 {

                private final Map<LevelType, Double> movementCost;

                private Builder2(final Map<LevelType, Double> movementCost) {
                    this.movementCost = movementCost;
                }

                public MobilityModule levelingCost(final Map<LevelType, Double> levelingCost) {
                    return new MobilityModule( resourceCost, movementSpeed, movementCost, levelingCost );
                }
            }
        }
    }
}
