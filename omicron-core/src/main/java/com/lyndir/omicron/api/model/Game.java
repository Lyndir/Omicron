package com.lyndir.omicron.api.model;

import static com.lyndir.omicron.api.model.CoreUtils.*;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.*;
import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.system.util.*;
import com.lyndir.omicron.api.GameListener;
import com.lyndir.omicron.api.util.PathUtils;
import java.util.*;
import javax.annotation.Nonnull;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
@SuppressWarnings("ParameterHidesMemberVariable") // IDEA doesn't understand setters that return this.
public class Game extends MetaObject implements IGame {

    static final Logger logger = Logger.get( IGame.class );

    private static final Random RANDOM = new Random();

    private Turn currentTurn;

    @ObjectMeta(ignoreFor = ObjectMeta.For.all)
    private final GameController gameController;

    private final Size                  levelSize;
    private final ImmutableList<Level>  levels;
    private final ImmutableList<Player> players;
    private final Set<Player> readyPlayers = new HashSet<>();
    private boolean running;

    private Game(final Size levelSize, final List<Player> players, final List<VictoryConditionType> victoryConditions,
                 final List<GameListener> gameListeners, final IGame.GameResourceConfig resourceConfig,
                 final IGame.GameUnitConfig unitConfig)
            throws Security.NotAuthenticatedException {
        this.levelSize = levelSize;
        levels = ImmutableList.of( new Level( levelSize, LevelType.GROUND, this ), new Level( levelSize, LevelType.SKY, this ),
                                   new Level( levelSize, LevelType.SPACE, this ) );
        this.players = ImmutableList.copyOf( players );
        currentTurn = new Turn();
        gameController = new GameController( this );

        for (final VictoryConditionType victoryCondition : victoryConditions)
            victoryCondition.install( this );
        for (final GameListener gameListener : gameListeners)
            gameController.addGameListener( gameListener );

        // Give each player some units.
        for (final Player player : players)
            unitConfig.addUnits( this, player, new UnitAdder() {
                @Override
                public void add(final IUnitType unitType, final ITile location) {
                    player.addObject( new GameObject( coreUT( unitType ), Game.this, player, coreT( location ) ) );
                }
            } );

        // Add resources to the tiles.
        // Figure out how many resources we're distributing for each of the resource types supported by our levels.
        Map<ResourceType, Integer> remainingResources = new EnumMap<>( ResourceType.class );
        remainingResources.putAll( FluentIterable.from( levels ).transformAndConcat( new Function<Level, Iterable<ResourceType>>() {
            @Override
            public Iterable<ResourceType> apply(final Level input) {
                return input.getType().getSupportedResources();
            }
        } ).toMap( new Function<ResourceType, Integer>() {
            @Override
            public Integer apply(final ResourceType input) {
                return resourceConfig.quantity( input );
            }
        } ) );
        while (true) {
            // Do we have remaining undistributed resources left?
            while (remainingResources.values().remove( 0 ))
                continue;
            if (remainingResources.isEmpty())
                break;

            // Go over our levels and distribute a puddle of remaining resources supported by them.
            for (final Level level : levels) {
                for (final ResourceType resourceType : level.getType().getSupportedResources()) {
                    Integer remaining = remainingResources.get( resourceType );
                    if (remaining == null || remaining == 0)
                        // No resources left to distribute for this type.
                        continue;

                    // Pick a spot to start a puddle, and determine the puddle tiles.
                    Tile location = level.getTile( RANDOM.nextInt( level.getSize().getWidth() ),
                                                   RANDOM.nextInt( level.getSize().getHeight() ) ).get();
                    Collection<Tile> puddle = PathUtils.neighbours( location, resourceConfig.puddleSize( resourceType ),
                                                                    new NNFunctionNN<Tile, Iterable<Tile>>() {
                                                                        @Nonnull
                                                                        @Override
                                                                        public Iterable<Tile> apply(@Nonnull final Tile input) {
                                                                            return input.neighbours();
                                                                        }
                                                                    } );

                    // Fill the puddle tiles with resource.
                    for (final Tile tile : puddle) {
                        int tileResources = Math.min( remaining, RANDOM.nextInt( resourceConfig.quantityPerTile( resourceType ) ) );
                        tile.addResourceQuantity( resourceType, tileResources );
                        remaining -= tileResources;
                        logger.trc( "Deposited %d %s at %s (%d left to deposit)", tileResources, resourceType, tile.getPosition(),
                                    remaining );
                    }

                    // Remember how much undistributed resource is left.
                    remainingResources.put( resourceType, remaining );
                }
            }
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public GameController getController() {
        return gameController;
    }

    @Override
    public Level getLevel(final LevelType levelType) {
        return FluentIterable.from( levels ).firstMatch( new Predicate<Level>() {
            @Override
            public boolean apply(final Level input) {
                return input.getType() == levelType;
            }
        } ).get();
    }

    @Override
    public Turn getCurrentTurn() {
        return currentTurn;
    }

    void setCurrentTurn(final Turn currentTurn) {
        this.currentTurn = currentTurn;
    }

    @Override
    public ImmutableList<Level> listLevels() {
        return levels;
    }

    @Override
    public ImmutableList<Player> getPlayers() {
        return players;
    }

    Set<Player> getReadyPlayers() {
        return readyPlayers;
    }

    void setRunning(final boolean running) {
        this.running = running;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public Size getLevelSize() {
        return levelSize;
    }

    public static class Builder implements IGame.IBuilder {

        private final List<GameListener>               gameListeners     = Lists.newLinkedList();
        private final List<IPlayer>                    players           = Lists.newArrayList();
        private final List<PublicVictoryConditionType> victoryConditions = Lists.newArrayList( PublicVictoryConditionType.values() );

        private Size                     levelSize      = new Size( 200, 200 );
        private int                      nextPlayerID   = 1;
        private int                      totalPlayers   = 4;
        private IGame.GameResourceConfig resourceConfig = IGame.GameResourceConfigs.PLENTY;
        private IGame.GameUnitConfig     unitConfig     = GameUnitConfigs.BASIC;

        private Builder() {
        }

        @Override
        public Game build()
                throws Security.NotAuthenticatedException {
            // Add random players until totalPlayers count is satisfied.
            while (players.size() < totalPlayers)
                addPlayer( new Player( nextPlayerID(), null, Player.randomName(), Color.Template.randomColor(),
                                       Color.Template.randomColor() ) );

            return new Game( levelSize, coreP( players ), coreVCT( victoryConditions ), gameListeners, resourceConfig, unitConfig );
        }

        @Override
        public Size getLevelSize() {
            return levelSize;
        }

        @Override
        public Builder setLevelSize(final Size levelSize) {
            this.levelSize = levelSize;

            return this;
        }

        @Override
        public List<IPlayer> getPlayers() {
            return players;
        }

        @Override
        public Builder addPlayer(final IPlayer player) {
            players.add( player );

            return this;
        }

        @Override
        public List<PublicVictoryConditionType> getVictoryConditions() {
            return victoryConditions;
        }

        @Override
        public Builder addVictoryCondition(final PublicVictoryConditionType victoryCondition) {
            victoryConditions.add( victoryCondition );

            return this;
        }

        @Override
        public Integer getTotalPlayers() {
            return totalPlayers;
        }

        @Override
        public Builder setTotalPlayers(final Integer totalPlayers) {
            this.totalPlayers = totalPlayers;

            return this;
        }

        @Override
        public Builder addGameListener(final GameListener gameListener) {
            gameListeners.add( gameListener );

            return this;
        }

        @Override
        public IGame.GameResourceConfig getResourceConfig() {
            return resourceConfig;
        }

        @Override
        public Builder setResourceConfig(final IGame.GameResourceConfig resourceConfig) {
            this.resourceConfig = resourceConfig;

            return this;
        }

        @Override
        public IGame.GameUnitConfig getUnitConfig() {
            return unitConfig;
        }

        @Override
        public Builder setUnitConfig(final IGame.GameUnitConfig unitConfig) {
            this.unitConfig = unitConfig;

            return this;
        }

        @Override
        public int nextPlayerID() {
            return nextPlayerID++;
        }
    }


    enum GameUnitConfigs implements IGame.GameUnitConfig {
        NONE {
            @Override
            public void addUnits(final IGame game, final IPlayer player, final IGame.UnitAdder unitAdder) {
            }
        },
        BASIC {
            @Override
            public void addUnits(final IGame game, final IPlayer player, final IGame.UnitAdder unitAdder) {
                Game coreGame = coreG( game );

                // Find tiles for the units.
                Tile startTileEngineer, startTileAirship, startTileScout;
                do {
                    Level ground = coreGame.getLevel( LevelType.GROUND );
                    startTileEngineer = ground.getTile( RANDOM.nextInt( ground.getSize().getWidth() ),
                                                        RANDOM.nextInt( ground.getSize().getHeight() ) ).get();

                    Level sky = coreGame.getLevel( LevelType.SKY );
                    Coordinate.Side randomSide = Coordinate.Side.values()[RANDOM.nextInt( Coordinate.Side.values().length )];
                    startTileAirship = sky.getTile( startTileEngineer.neighbour( randomSide ).getPosition() ).get();

                    randomSide = Coordinate.Side.values()[RANDOM.nextInt( Coordinate.Side.values().length )];
                    startTileScout = startTileEngineer.neighbour( randomSide );
                }
                while (startTileEngineer.getContents().isPresent() || //
                       startTileAirship.getContents().isPresent() || //
                       startTileScout.getContents().isPresent());

                // Add the units.
                unitAdder.add( UnitTypes.ENGINEER, startTileEngineer );
                unitAdder.add( UnitTypes.AIRSHIP, startTileAirship );
                unitAdder.add( UnitTypes.SCOUT, startTileScout );
            }
        }
    }
}
