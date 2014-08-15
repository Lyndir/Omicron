package com.lyndir.omicron.api;

import com.google.common.collect.*;
import com.lyndir.lhunath.opal.math.*;
import com.lyndir.lhunath.opal.system.error.AlreadyCheckedException;
import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.system.util.*;
import com.lyndir.omicron.api.error.NotAuthenticatedException;
import com.lyndir.omicron.api.util.PathUtils;
import java.util.*;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
@SuppressWarnings("ParameterHidesMemberVariable") // IDEA doesn't understand setters that return this.
public class Game extends MetaObject implements IGame {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger logger = Logger.get( Game.class );

    private static final Random RANDOM = new Random();

    private final Deque<Turn> turns = new ConcurrentLinkedDeque<>();

    @ObjectMeta(ignoreFor = ObjectMeta.For.all)
    private final GameController gameController;

    private final Size                  levelSize;
    private final ImmutableList<Level>  levels;
    private final ImmutableList<Player> players;
    private final Set<Player> readyPlayers = Collections.synchronizedSet( new HashSet<>() );
    private boolean running;

    public static Builder builder() {
        return new Builder();
    }

    private Game(final Size levelSize, final Iterable<Player> players, final Stream<VictoryConditionType> victoryConditions,
                 final Map<GameListener, Player> gameListeners, final GameResourceConfig resourceConfig, final GameUnitConfig unitConfig)
            throws NotAuthenticatedException {
        turns.add( new Turn() );
        this.levelSize = levelSize;
        levels = ImmutableList.of( new Level( levelSize, LevelType.GROUND ), new Level( levelSize, LevelType.SKY ),
                                   new Level( levelSize, LevelType.SPACE ) );
        this.players = ImmutableList.copyOf( players );
        gameController = new GameController( this );

        for (Iterator<VictoryConditionType> iterator = victoryConditions.iterator(); iterator.hasNext(); )
            iterator.next().install( this );
        gameController.addGameListeners( gameListeners );
        Security.activateGame( this );

        // Add resources to the tiles.
        // Figure out how many resources we're distributing for each of the resource types supported by our levels.
        Map<ResourceType, Integer> remainingResources = new EnumMap<>( ResourceType.class );
        remainingResources.putAll( FluentIterable.from( levels )
                                                 .transformAndConcat( level -> level.getType().getSupportedResources() )
                                                 .toMap( resourceConfig::quantity ) );
        while (true) {
            // Do we have remaining undistributed resources left?
            while (remainingResources.values().remove( 0 ))
                ;
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
                    Vec2 position = Vec2.create( RANDOM.nextInt( level.getSize().getWidth() ),
                                                 RANDOM.nextInt( level.getSize().getHeight() ) );
                    Collection<? extends ITile> puddle = PathUtils.neighbours( level.getTile( position ).get(),
                                                                               resourceConfig.puddleSize( resourceType ),
                                                                               ITile::neighbours );

                    // Fill the puddle tiles with resource.
                    for (final ITile tile : puddle) {
                        int tileResources = Math.min( remaining, RANDOM.nextInt( resourceConfig.quantityPerTile( resourceType ) ) );
                        Tile.cast( tile ).addResourceQuantity( resourceType, tileResources );
                        remaining -= tileResources;
                        logger.trc( "Deposited %d %s at %s (%d left to deposit)", tileResources, resourceType, tile.getPosition(),
                                    remaining );
                    }

                    // Remember how much undistributed resource is left.
                    remainingResources.put( resourceType, remaining );
                }
            }
        }

        // Give each player some units.
        for (final Player player : players)
            unitConfig.addUnits( this, player );
    }

    @Override
    public int hashCode() {
        return System.identityHashCode( this );
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        return obj == this;
    }

    @Override
    public GameController getController() {
        return gameController;
    }

    @Override
    public Deque<Turn> getTurns() {
        return turns;
    }

    Turn newTurn() {
        readyPlayers.clear();

        Turn newTurn = new Turn( turns.getLast() );
        turns.add( newTurn );
        return newTurn;
    }

    @Override
    public ImmutableList<Player> getPlayers() {
        return players;
    }

    @Override
    public ImmutableSet<Player> getReadyPlayers() {
        return ImmutableSet.copyOf( readyPlayers );
    }

    boolean setReady(final Player player) {
        synchronized (readyPlayers) {
            return readyPlayers.add( player ) && readyPlayers.containsAll( getPlayers() );
        }
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

    @Override
    public ImmutableList<? extends ILevel> getLevels() {
        return levels;
    }

    public static class Builder implements IBuilder {

        private final Map<GameListener, Player>        gameListeners     = Maps.newLinkedHashMap();
        private final List<Player>                     players           = Lists.newLinkedList();
        private final List<PublicVictoryConditionType> victoryConditions = Lists.newArrayList( PublicVictoryConditionType.values() );

        private Size                 levelSize      = new Size( 200, 200 );
        private int                  nextPlayerID   = 1;
        private int                  totalPlayers   = 4;
        private GameResourceConfig   resourceConfig = GameResourceConfigs.PLENTY;
        private PublicGameUnitConfig unitConfig     = PublicGameUnitConfig.BASIC;

        private Builder() {
        }

        @Override
        public Game build() {
            return Security.godRun( () -> {
                // Add random players until totalPlayers count is satisfied.
                while (players.size() < totalPlayers)
                    players.add( new Player( nextPlayerID(), null, Player.randomName(), //
                                             Color.Template.randomColor(), Color.Template.randomColor() ) );

                return new Game( levelSize, players, VictoryConditionType.cast( victoryConditions ), gameListeners, resourceConfig,
                                 GameUnitConfig.cast( unitConfig ) );
            } );
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
        public Collection<? extends IPlayer> getPlayers() {
            return players;
        }

        @Override
        public Builder setPlayer(final PlayerKey playerKey, final String name, final Color primaryColor, final Color secondaryColor) {
            Player existingPlayer = Iterables.find( players, player -> player.hasKey( playerKey ), null );
            if (existingPlayer != null)
                players.remove( existingPlayer );
            players.add( new Player( nextPlayerID(), playerKey, name, primaryColor, secondaryColor ) );

            return this;
        }

        @Override
        public Player addPlayer(final PlayerKey playerKey, final String name, final Color primaryColor, final Color secondaryColor) {
            Player player = new Player( nextPlayerID(), playerKey, name, primaryColor, secondaryColor );
            players.add( player );

            return player;
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
            gameListeners.put( gameListener, Security.currentPlayer() );

            return this;
        }

        @Override
        public GameResourceConfig getResourceConfig() {
            return resourceConfig;
        }

        @Override
        public Builder setResourceConfig(final GameResourceConfig resourceConfig) {
            this.resourceConfig = resourceConfig;

            return this;
        }

        @Override
        public PublicGameUnitConfig getUnitConfig() {
            return unitConfig;
        }

        @Override
        public Builder setUnitConfig(final PublicGameUnitConfig unitConfig) {
            this.unitConfig = unitConfig;

            return this;
        }

        @Override
        public int nextPlayerID() {
            return nextPlayerID++;
        }
    }


    enum GameUnitConfig {
        NONE {
            @Override
            void addUnits(final Game game, final Player player) {
            }
        },
        BASIC {
            @Override
            void addUnits(final Game game, final Player player) {
                // Find tiles for the units.
                ILevel ground = game.getLevel( LevelType.GROUND );
                ILevel sky = game.getLevel( LevelType.SKY );
                Optional<? extends ITile> engineerTile, airshipTile, scoutTile;
                while (true) {
                    Vec2 engineerPosition = Vec2.create( RANDOM.nextInt( ground.getSize().getWidth() ),
                                                         RANDOM.nextInt( ground.getSize().getHeight() ) );

                    Side randomSide = Side.values()[RANDOM.nextInt( Side.values().length )];
                    Vec2 airshipPosition = engineerPosition.translate( randomSide.getDelta() );

                    randomSide = Side.values()[RANDOM.nextInt( Side.values().length )];
                    Vec2 scoutPosition = engineerPosition.translate( randomSide.getDelta() );

                    engineerTile = ground.getTile( engineerPosition );
                    if (!engineerTile.isPresent() || engineerTile.get().getContents().isPresent() || //
                        !engineerTile.get().getResourceQuantity( ResourceType.METALS ).isPresent())
                        continue;
                    airshipTile = sky.getTile( airshipPosition );
                    if (!airshipTile.isPresent() || airshipTile.get().getContents().isPresent())
                        continue;
                    scoutTile = ground.getTile( scoutPosition );
                    if (!scoutTile.isPresent() || scoutTile.get().getContents().isPresent())
                        continue;

                    break;
                }

                // Add the units.
                GameObject engineer = new GameObject( UnitTypes.ENGINEER, game, player, Tile.cast( engineerTile.get() ) );
                engineer.onModule( ModuleType.CONTAINER, module -> module.getResourceType() == ResourceType.METALS )
                        .addStock( Integer.MAX_VALUE );
                engineer.register();

                new GameObject( UnitTypes.AIRSHIP, game, player, Tile.cast( airshipTile.get() ) ).register();
                new GameObject( UnitTypes.SCOUT, game, player, Tile.cast( scoutTile.get() ) ).register();
            }
        };

        abstract void addUnits(final Game game, final Player player);

        static GameUnitConfig cast(final PublicGameUnitConfig unitConfig) {
            switch (unitConfig) {
                case NONE:
                    return NONE;
                case BASIC:
                    return BASIC;
            }

            throw new AlreadyCheckedException();
        }
    }
}
