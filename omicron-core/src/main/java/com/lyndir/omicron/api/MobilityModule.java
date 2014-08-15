package com.lyndir.omicron.api;

import static com.lyndir.lhunath.opal.system.util.ObjectUtils.*;
import static com.lyndir.omicron.api.error.ExceptionUtils.*;
import static com.lyndir.omicron.api.util.PathUtils.*;

import com.lyndir.lhunath.opal.system.util.*;
import com.lyndir.omicron.api.error.*;
import java.util.*;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class MobilityModule extends Module implements IMobilityModule, IMobilityModuleController {

    private final int movementSpeed;
    private final Map<LevelType, Double> movementCost = Collections.synchronizedMap( new EnumMap<>( LevelType.class ) );
    private final Map<LevelType, Double> levelingCost = Collections.synchronizedMap( new EnumMap<>( LevelType.class ) );

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
            throws NotAuthenticatedException, NotObservableException {
        assertObservable();

        return remainingSpeed;
    }

    @Override
    public double getMovementSpeed() {
        assertObservable();

        return movementSpeed;
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
            throws NotAuthenticatedException, NotObservableException {
        assertObservable();

        return ifNotNullElse( movementCost.get( levelType ), Double.MAX_VALUE );
    }

    @Override
    public double costForLevelingToLevel(final LevelType levelType)
            throws NotAuthenticatedException, NotObservableException {
        assertObservable();
        Tile location = getGameObject().getLocation().get();

        // Level up until we reach the target level.
        double cost = 0;
        LevelType currentLevel = location.getLevel().getType();
        if (levelType == currentLevel)
            return 0;
        while (true) {
            Double currentLevelCost = levelingCost.get( currentLevel );
            if (currentLevelCost == null)
                // Cannot level to this level.
                return Double.MAX_VALUE;

            Optional<LevelType> newLevel = currentLevel.up();
            if (!newLevel.isPresent())
                break;

            currentLevel = newLevel.get();
            cost += currentLevelCost;

            if (currentLevel == levelType)
                return cost;
        }

        // Level down until we reach the target level.
        cost = 0;
        currentLevel = location.getLevel().getType();
        while (true) {
            Double currentLevelCost = levelingCost.get( currentLevel );
            if (currentLevelCost == null)
                // Cannot level to this level.
                return Double.MAX_VALUE;

            Optional<LevelType> newLevel = currentLevel.down();
            if (!newLevel.isPresent())
                break;

            currentLevel = newLevel.get();
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
    public Leveling leveling(final LevelType levelType)
            throws NotAuthenticatedException, NotOwnedException, NotObservableException {
        assertOwned();

        Tile currentLocation = getGameObject().getLocation().get();
        if (levelType == currentLocation.getLevel().getType())
            // Already in the destination level.
            return Leveling.possible( this, currentLocation, 0 );

        double cost = costForLevelingToLevel( levelType );
        if (cost > remainingSpeed)
            // Cannot move: insufficient speed remaining this turn.
            return Leveling.impossible( this, cost );

        return Leveling.possible( this,
                                  getGameObject().getGame().getLevel( levelType ).getTile( currentLocation.getPosition() ).get(),
                                  cost );
    }

    /**
     * Move the unit to an adjacent tile.
     *
     * @param target The side of the adjacent tile relative to the current.
     */
    @Override
    public Movement movement(final ITile target)
            throws NotAuthenticatedException, NotOwnedException, NotObservableException {
        assertOwned();

        Leveling leveling = leveling( target.getLevel().getType() );
        if (!leveling.isPossible())
            // Cannot move because we can't level to the target's level.
            return Movement.impossible( this, leveling.getCost() );

        // Initialize cost calculation.
        ITile currentLocation = leveling.getTarget();
        final double stepCost = costForMovingInLevel( currentLocation.getLevel().getType() );

        // Initialize path finding data functions.
        PredicateNN<ITile> foundFunction = tile -> isEqual( tile, target );
        NNFunctionNN<Step<ITile>, Double> costFunction = tileStep -> {
            if (!tileStep.getTo().isAccessible().isTrue())
                return Double.MAX_VALUE;

            return stepCost;
        };
        NNFunctionNN<ITile, Stream<? extends ITile>> neighboursFunction = (input) -> input.neighbours().stream();

        // Find the path!
        Optional<Path<ITile>> path = find( currentLocation, foundFunction, costFunction, remainingSpeed, neighboursFunction );
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
    public IMobilityModuleController getController() {
        return this;
    }

    @Override
    public IMobilityModule getModule() {
        return this;
    }

    public static class Leveling extends MetaObject implements IMobilityModuleController.ILeveling {

        private final MobilityModule module;
        private final double cost;
        private final Optional<ITile> target;

        private Leveling(final MobilityModule module, final Optional<ITile> target, final double cost) {
            this.module = module;
            this.cost = cost;
            this.target = target;
        }

        static Leveling impossible(final MobilityModule module, final double cost) {
            return new Leveling( module, Optional.empty(), cost );
        }

        static Leveling possible(final MobilityModule module, final ITile target, final double cost) {
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
        public ITile getTarget() {
            return target.get();
        }

        @Override
        public void execute()
                throws NotAuthenticatedException, NotOwnedException, ImpossibleException, InvalidatedException {
            module.assertOwned();
            assertState( isPossible(), ImpossibleException.class );
            assertState( cost <= module.remainingSpeed, InvalidatedException.class );

            // TODO: No target.isAccessible check: Most units that level cannot see other levels before they go there.
            // TODO: Should we disallow leveling until you can see the level above you like we do with movement and the tile you move to?
            Change.From<ITile> locationChange = Change.<ITile>from( module.getGameObject().getLocation().get() );
            ChangeDbl.From remainingSpeedChange = ChangeDbl.from( module.remainingSpeed );

            // Execute the leveling.
            module.getGameObject().getController().setLocation( Tile.cast( target.get() ) );
            module.remainingSpeed -= cost;

            module.getGameObject().getGame().getController()
                  .fireIfObservable( module.getGameObject() )
                  .onMobilityLeveled( module, locationChange.to( module.getGameObject().getLocation().get() ),
                                      remainingSpeedChange.to( module.remainingSpeed ) );
        }
    }


    public static class Movement extends MetaObject implements IMovement {

        private final MobilityModule       module;
        private final double               cost;
        private final Leveling             leveling;
        private final Optional<Path<ITile>> path;

        private Movement(final MobilityModule module, final double cost, @Nullable final Leveling leveling,
                         final Optional<Path<ITile>> path) {
            this.module = module;
            this.cost = cost;
            this.leveling = leveling;
            this.path = path;
        }

        static Movement impossible(final MobilityModule module, final double cost) {
            return new Movement( module, cost, null, Optional.empty() );
        }

        static Movement possible(final MobilityModule module, final double cost, @Nonnull final Leveling leveling,
                                 final Optional<Path<ITile>> path) {
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
        public Path<ITile> getPath() {
            return path.get();
        }

        @Override
        public boolean isPossible() {
            return path.isPresent();
        }

        @Override
        public void execute()
                throws NotAuthenticatedException, NotOwnedException, ImpossibleException, InvalidatedException {
            module.assertOwned();
            assertState( isPossible(), ImpossibleException.class );
            assertState( cost <= module.remainingSpeed, InvalidatedException.class );
            assert leveling != null;

            Change.From<ITile> locationChange = Change.<ITile>from( module.getGameObject().getLocation().get() );
            ChangeDbl.From remainingSpeedChange = ChangeDbl.from( module.remainingSpeed );

            // Check that the path can still be walked.
            Path<ITile> tracePath = path.get();
            do {
                assertState( tracePath.getTarget().isAccessible().isTrue() || //
                             isEqual( module.getGameObject().getLocation(), tracePath.getTarget() ), //
                             PathInvalidatedException.class, tracePath
                );

                Optional<Path<ITile>> parent = tracePath.getParent();
                if (!parent.isPresent())
                    break;

                tracePath = parent.get();
            }
            while (true);

            // Execute the leveling.
            leveling.execute();

            // Execute the path.
            module.getGameObject().getController().setLocation( Tile.cast( path.get().getTarget() ) );
            module.remainingSpeed -= path.get().getCost();

            module.getGameObject().getGame().getController()
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
