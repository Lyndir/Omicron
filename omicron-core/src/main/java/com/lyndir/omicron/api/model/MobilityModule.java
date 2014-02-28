package com.lyndir.omicron.api.model;

import static com.lyndir.lhunath.opal.system.util.ObjectUtils.*;
import static com.lyndir.omicron.api.model.error.ExceptionUtils.*;
import static com.lyndir.omicron.api.util.PathUtils.*;

import com.google.common.base.Optional;
import com.lyndir.lhunath.opal.system.util.*;
import com.lyndir.omicron.api.*;
import java.util.*;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class MobilityModule extends Module implements IMobilityModule {

    private final int movementSpeed;
    private final Map<LevelType, Double> movementCost = Collections.synchronizedMap( new EnumMap<LevelType, Double>( LevelType.class ) );
    private final Map<LevelType, Double> levelingCost = Collections.synchronizedMap( new EnumMap<LevelType, Double>( LevelType.class ) );

    private double remainingSpeed;

    protected MobilityModule(final ImmutableResourceCost resourceCost, final int movementSpeed, final Map<LevelType, Double> movementCost,
                             final Map<LevelType, Double> levelingCost) {
        super( resourceCost );

        this.movementSpeed = movementSpeed;
        this.movementCost.putAll( movementCost );
        this.levelingCost.putAll( levelingCost );
    }

    static Builder0 createWithStandardResourceCost() {
        return createWithExtraResourceCost( ResourceCost.immutable() );
    }

    static Builder0 createWithExtraResourceCost(final ImmutableResourceCost resourceCost) {
        return new Builder0( ModuleType.MOBILITY.getStandardCost().add( resourceCost ) );
    }

    @Override
    public double getRemainingSpeed()
            throws Security.NotAuthenticatedException, Security.NotObservableException {
        assertObservable();

        return remainingSpeed;
    }

    /**
     * Get the speed cost related to moving around in the given level.
     *
     * @param levelType The level to move around in.
     *
     * @return The speed cost.
     */
    @Override
    public double costForMovingInLevel(final LevelType levelType)
            throws Security.NotAuthenticatedException, Security.NotObservableException {
        assertObservable();

        return ifNotNullElse( movementCost.get( levelType ), Double.MAX_VALUE );
    }

    /**
     * Get the speed cost related to leveling from the current level type to the given level type.
     *
     * @param levelType The level to transition to.
     *
     * @return The speed cost.
     */
    @Override
    public double costForLevelingToLevel(final LevelType levelType)
            throws Security.NotAuthenticatedException, Security.NotObservableException {
        assertObservable();
        Optional<Tile> location = getGameObject().getLocation();
        if (!location.isPresent())
            return Double.MAX_VALUE;

        // Level up until we reach the target level.
        double cost = 0;
        LevelType currentLevel = location.get().getLevel().getType();
        if (levelType == currentLevel)
            return 0;
        while (true) {
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

        // Level down until we reach the target level.
        cost = 0;
        currentLevel = location.get().getLevel().getType();
        while (true) {
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

        // Unreachable code.
        throw new IllegalArgumentException( "Unsupported level type: " + levelType );
    }

    /**
     * Move the unit to the given level.
     *
     * @param levelType The side of the adjacent tile relative to the current.
     */
    @Override
    @Authenticated
    public Leveling leveling(final LevelType levelType)
            throws Security.NotAuthenticatedException, Security.NotOwnedException, Security.NotObservableException {
        assertOwned();

        Optional<Tile> currentLocation = getGameObject().getLocation();
        if (!currentLocation.isPresent())
            return Leveling.impossible( this, 0 );

        if (levelType == currentLocation.get().getLevel().getType())
            // Already in the destination level.
            return Leveling.possible( this, currentLocation.get(), 0 );

        double cost = costForLevelingToLevel( levelType );
        if (cost > remainingSpeed)
            // Cannot move: insufficient speed remaining this turn.
            return Leveling.impossible( this, cost );

        return Leveling.possible( this,
                                  getGameObject().getGame().getLevel( levelType ).getTile( currentLocation.get().getPosition() ).get(),
                                  cost );
    }

    /**
     * Move the unit to an adjacent tile.
     *
     * @param target The side of the adjacent tile relative to the current.
     */
    @Override
    @Authenticated
    public Movement movement(final ITile target)
            throws Security.NotAuthenticatedException, Security.NotOwnedException, Security.NotObservableException {
        assertOwned();

        Leveling leveling = leveling( target.getLevel().getType() );
        if (!leveling.isPossible())
            // Cannot move because we can't level to the target's level.
            return Movement.impossible( this, leveling.getCost() );

        // Initialize cost calculation.
        Tile currentLocation = leveling.getTarget();
        final double stepCost = costForMovingInLevel( currentLocation.getLevel().getType() );

        // Initialize path finding data functions.
        PredicateNN<Tile> foundFunction = new PredicateNN<Tile>() {
            @Override
            public boolean apply(@Nonnull final Tile tile) {
                return isEqual( tile, target );
            }
        };
        NNFunctionNN<Step<Tile>, Double> costFunction = new NNFunctionNN<Step<Tile>, Double>() {
            @Nonnull
            @Override
            public Double apply(@Nonnull final Step<Tile> tileStep) {
                if (!tileStep.getTo().checkAccessible())
                    return Double.MAX_VALUE;

                return stepCost;
            }
        };
        NNFunctionNN<Tile, Iterable<Tile>> neighboursFunction = new NNFunctionNN<Tile, Iterable<Tile>>() {
            @Nonnull
            @Override
            public Iterable<Tile> apply(@Nonnull final Tile tile) {
                return tile.neighbours();
            }
        };

        // Find the path!
        Optional<Path<Tile>> path = find( currentLocation, foundFunction, costFunction, remainingSpeed, neighboursFunction );
        return Movement.possible( this, leveling.getCost() + (path.isPresent()? path.get().getCost(): 0), leveling, path );
    }

    @Override
    protected void onReset() {
        remainingSpeed = movementSpeed;
    }

    @Override
    protected void onNewTurn() {
    }

    @Override
    public ModuleType<MobilityModule> getType() {
        return ModuleType.MOBILITY;
    }

    public static class Leveling extends MetaObject implements ILeveling {

        private final MobilityModule module;
        private final double cost;
        private final Optional<Tile> target;

        private Leveling(final MobilityModule module, final Optional<Tile> target, final double cost) {
            this.module = module;
            this.cost = cost;
            this.target = target;
        }

        static Leveling impossible(final MobilityModule module, final double cost) {
            return new Leveling( module, Optional.<Tile>absent(), cost );
        }

        static Leveling possible(final MobilityModule module, final Tile target, final double cost) {
            return new Leveling( module, Optional.of( target ), cost );
        }

        @Override
        public boolean isPossible() {
            return target.isPresent();
        }

        /**
         * The cost for executing the leveling.  If not possible, the cost is either zero if unknown or the cost for the action that
         * exceeded the module's remaining speed (not the cost of getting to the target).
         *
         * @return An amount of speed.
         */
        @Override
        public double getCost() {
            return cost;
        }

        /**
         * @return The target tile after leveling.
         *
         * @throws IllegalStateException if the leveling is not possible ({@link #isPossible()} returns {@code false})
         */
        @Override
        public Tile getTarget() {
            return target.get();
        }

        @Override
        @Authenticated
        public void execute()
                throws Security.NotAuthenticatedException, Security.NotOwnedException, ImpossibleException, InvalidatedException {
            module.assertOwned();
            assertState( isPossible(), ImpossibleException.class );
            assertState( cost <= module.remainingSpeed, InvalidatedException.class );

            // TODO: No target.isAccessible check: Most units that level cannot see other levels before they go there.
            // TODO: Should we disallow leveling until you can see the level above you like we do with movement and the tile you move to?
            Change.From<ITile> locationChange = Change.<ITile>from( module.getGameObject().getLocation().get() );
            ChangeDbl.From remainingSpeedChange = ChangeDbl.from( module.remainingSpeed );

            // Execute the leveling.
            module.getGameObject().getController().setLocation( target.get() );
            module.remainingSpeed -= cost;

            module.getGameController()
                  .fireIfObservable( module.getGameObject() )
                  .onMobilityLeveled( module, locationChange.to( module.getGameObject().getLocation().get() ),
                                      remainingSpeedChange.to( module.remainingSpeed ) );
        }
    }


    public static class Movement extends MetaObject implements IMovement {

        private final MobilityModule       module;
        private final double               cost;
        private final Leveling             leveling;
        private final Optional<Path<Tile>> path;

        private Movement(final MobilityModule module, final double cost, @Nullable final Leveling leveling,
                         final Optional<Path<Tile>> path) {
            this.module = module;
            this.cost = cost;
            this.leveling = leveling;
            this.path = path;
        }

        static Movement impossible(final MobilityModule module, final double cost) {
            return new Movement( module, cost, null, Optional.<Path<Tile>>absent() );
        }

        static Movement possible(final MobilityModule module, final double cost, @Nonnull final Leveling leveling,
                                 final Optional<Path<Tile>> path) {
            return new Movement( module, cost, leveling, path );
        }

        /**
         * The cost for executing the movement.  If not possible, the cost is either zero if unknown or the cost for the action that
         * exceeded the module's remaining speed (not the cost of getting to the target).
         *
         * @return An amount of speed.
         */
        @Override
        public double getCost() {
            return cost;
        }

        /**
         * @return The target tile after leveling.
         *
         * @throws IllegalStateException if the leveling is not possible ({@link #isPossible()} returns {@code false})
         */
        @Override
        public Path<Tile> getPath() {
            return path.get();
        }

        @Override
        public boolean isPossible() {
            return path.isPresent();
        }

        @Override
        @Authenticated
        public void execute()
                throws Security.NotAuthenticatedException, Security.NotOwnedException, ImpossibleException, InvalidatedException {
            module.assertOwned();
            assertState( isPossible(), ImpossibleException.class );
            assertState( cost <= module.remainingSpeed, InvalidatedException.class );
            assert leveling != null;

            Change.From<ITile> locationChange = Change.<ITile>from( module.getGameObject().getLocation().get() );
            ChangeDbl.From remainingSpeedChange = ChangeDbl.from( module.remainingSpeed );

            // Check that the path can still be walked.
            Path<Tile> tracePath = path.get();
            do {
                assertState( tracePath.getTarget().checkAccessible() || //
                             isEqual( module.getGameObject().getLocation().get(), tracePath.getTarget() ), //
                             PathInvalidatedException.class, tracePath
                );

                Optional<Path<Tile>> parent = tracePath.getParent();
                if (!parent.isPresent())
                    break;

                tracePath = parent.get();
            }
            while (true);

            // Execute the leveling.
            leveling.execute();

            // Execute the path.
            module.getGameObject().getController().setLocation( path.get().getTarget() );
            module.remainingSpeed -= path.get().getCost();

            module.getGameController()
                  .fireIfObservable( module.getGameObject() )
                  .onMobilityMoved( module, locationChange.to( module.getGameObject().getLocation().get() ),
                                    remainingSpeedChange.to( module.remainingSpeed ) );
        }
    }


    @SuppressWarnings({ "ParameterHidesMemberVariable", "InnerClassFieldHidesOuterClassField" })
    static class Builder0 {

        private final ImmutableResourceCost resourceCost;

        private Builder0(final ImmutableResourceCost resourceCost) {
            this.resourceCost = resourceCost;
        }

        Builder1 movementSpeed(final int movementSpeed) {
            return new Builder1( movementSpeed );
        }

        class Builder1 {

            private final int movementSpeed;

            private Builder1(final int movementSpeed) {
                this.movementSpeed = movementSpeed;
            }

            Builder2 movementCost(final Map<LevelType, Double> movementCost) {
                return new Builder2( movementCost );
            }

            class Builder2 {

                private final Map<LevelType, Double> movementCost;

                private Builder2(final Map<LevelType, Double> movementCost) {
                    this.movementCost = movementCost;
                }

                MobilityModule levelingCost(final Map<LevelType, Double> levelingCost) {
                    return new MobilityModule( resourceCost, movementSpeed, movementCost, levelingCost );
                }
            }
        }
    }
}
