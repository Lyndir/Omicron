package com.lyndir.omicron.api.model;

import static com.lyndir.lhunath.opal.system.util.ObjectUtils.*;
import static com.lyndir.omicron.api.util.PathUtils.*;

import com.google.common.base.Optional;
import com.lyndir.lhunath.opal.system.util.*;
import java.util.EnumMap;
import java.util.Map;
import javax.annotation.Nonnull;


public class MobilityModule extends Module {

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

    public double getRemainingSpeed() {
        return remainingSpeed;
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

        // Level up until we reach the target level.
        double cost = 0;
        LevelType currentLevel = getGameObject().getLocation().getLevel().getType();
        if (levelType == currentLevel)
            return 0;
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
        currentLevel = getGameObject().getLocation().getLevel().getType();
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
     * Move the unit to the given level.
     *
     * @param currentPlayer The player ordering the action.
     * @param levelType     The side of the adjacent tile relative to the current.
     */
    public Leveling leveling(final Player currentPlayer, final LevelType levelType) {
        Tile currentLocation = getGameObject().getLocation();
        if (levelType == currentLocation.getLevel().getType())
            // Already in the destination level.
            return new Leveling( 0, currentLocation );

        if (!ObjectUtils.isEqual( currentPlayer, getGameObject().getOwner().orNull() ))
            // Cannot level object that doesn't belong to the current player.
            return new Leveling( 0 );

        double cost = costForLevelingToLevel( levelType );
        if (cost > remainingSpeed)
            // Cannot move: insufficient speed remaining this turn.
            return new Leveling( cost );

        return new Leveling( cost, getGameObject().getGame().getLevel( levelType ).getTile( currentLocation.getPosition() ).get() );
    }

    /**
     * Move the unit to an adjacent tile.
     *
     * @param currentPlayer The player ordering the action.
     * @param target        The side of the adjacent tile relative to the current.
     */
    public Movement movement(final Player currentPlayer, final Tile target) {
        if (!ObjectUtils.isEqual( currentPlayer, getGameObject().getOwner().orNull() ))
            // Cannot move object that doesn't belong to the current player.
            return new Movement( 0 );

        Leveling leveling = leveling( currentPlayer, target.getLevel().getType() );
        if (!leveling.isPossible())
            // Cannot move because we can't level to the target's level.
            return new Movement( leveling.getCost() );

        // Initialize cost calculation.
        Tile currentLocation = leveling.getTarget();
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
        Optional<Path<Tile>> path = find( currentLocation, foundFunction, costFunction, Double.MAX_VALUE, neighboursFunction );
        return new Movement( leveling.getCost() + (path.isPresent()? path.get().getCost(): 0), leveling, path );
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

        private final double         cost;
        private final Optional<Tile> target;

        Leveling(final double cost) {
            this.cost = cost;
            target = Optional.absent();
        }

        Leveling(final double cost, final Tile target) {
            this.cost = cost;
            this.target = Optional.of( target );
        }

        public boolean isPossible() {
            return target.isPresent();
        }

        /**
         * The cost for executing the leveling.  If not possible, the cost is either zero if unknown or the cost for the action that
         * exceeded the module's remaining speed (not the cost of getting to the target).
         *
         * @return An amount of speed.
         */
        public double getCost() {
            return cost;
        }

        /**
         * @return The target tile after leveling.
         *
         * @throws IllegalStateException if the leveling is not possible ({@link #isPossible()} returns {@code false})
         */
        public Tile getTarget() {
            return target.get();
        }

        public boolean execute() {
            if (!isPossible())
                return false;

            remainingSpeed -= cost;
            getGameObject().getController().setLocation( target.get() );

            return true;
        }
    }


    public class Movement {

        private final double               cost;
        private final Leveling             leveling;
        private final Optional<Path<Tile>> path;

        Movement(final double cost) {
            this.cost = cost;
            leveling = null;
            path = Optional.absent();
        }

        Movement(final double cost, @Nonnull final Leveling leveling, final Optional<Path<Tile>> path) {
            this.cost = cost;
            this.leveling = leveling;
            this.path = path;
        }

        /**
         * The cost for executing the movement.  If not possible, the cost is either zero if unknown or the cost for the action that
         * exceeded the module's remaining speed (not the cost of getting to the target).
         *
         * @return An amount of speed.
         */
        public double getCost() {
            return cost;
        }

        /**
         * @return The target tile after leveling.
         *
         * @throws IllegalStateException if the leveling is not possible ({@link #isPossible()} returns {@code false})
         */
        public Path<Tile> getPath() {
            return path.get();
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
                if (!tracePath.getTarget().isAccessible() && !ObjectUtils.isEqual( tracePath.getTarget(), getGameObject().getLocation() ))
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
