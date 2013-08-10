package com.lyndir.omicron.api.model;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.*;
import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.system.util.*;
import com.lyndir.omicron.api.util.PathUtils;
import java.util.*;
import javax.annotation.Nonnull;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
@SuppressWarnings("ParameterHidesMemberVariable") // IDEA doesn't understand setters that return this.
public class Game extends MetaObject {

    static final Logger logger = Logger.get( Game.class );

    private static final Random RANDOM = new Random();

    private Turn currentTurn;

    @ObjectMeta(ignoreFor = ObjectMeta.For.all)
    private final GameController gameController;

    private final Size                  levelSize;
    private final ImmutableList<Level>  levels;
    private final ImmutableList<Player> players;
    private final Set<Player> readyPlayers = new HashSet<>();
    private boolean running;

    private Game(final Size levelSize, final ImmutableList<Player> players, final GameResourceConfig resourceConfig,
                 final GameUnitConfig unitConfig) {

        this.levelSize = levelSize;
        levels = ImmutableList.of( new Level( levelSize, LevelType.GROUND, this ), new Level( levelSize, LevelType.SKY, this ),
                                   new Level( levelSize, LevelType.SPACE, this ) );
        this.players = players;
        currentTurn = new Turn();
        gameController = new GameController( this );

        // Give each player some units.
        for (final Player player : players)
            unitConfig.addUnits( this, player );

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

    public GameController getController() {

        return gameController;
    }

    public Level getLevel(final LevelType levelType) {

        return FluentIterable.from( levels ).firstMatch( new Predicate<Level>() {
            @Override
            public boolean apply(final Level input) {

                return input.getType() == levelType;
            }
        } ).get();
    }

    public Turn getCurrentTurn() {

        return currentTurn;
    }

    void setCurrentTurn(final Turn currentTurn) {

        this.currentTurn = currentTurn;
    }

    public ImmutableList<Level> listLevels() {

        return levels;
    }

    public ImmutableList<Player> getPlayers() {

        return players;
    }

    Set<Player> getReadyPlayers() {

        return readyPlayers;
    }

    void setRunning(final boolean running) {
        this.running = running;
    }

    public boolean isRunning() {
        return running;
    }

    public Size getLevelSize() {
        return levelSize;
    }

    public static class Builder {

        private Size               levelSize      = new Size( 200, 200 );
        private List<Player>       players        = Lists.newLinkedList();
        private int                nextPlayerID   = 1;
        private int                totalPlayers   = 4;
        private GameResourceConfig resourceConfig = GameResourceConfigs.PLENTY;
        private GameUnitConfig     unitConfig     = GameUnitConfigs.BASIC;

        private Builder() {
        }

        public Game build() {

            // Add random players until totalPlayers count is satisfied.
            int playerID = nextPlayerID;
            while (players.size() < totalPlayers) {
                Player randomPlayer = new Player( playerID++, null, Player.randomName(), Color.Template.randomColor(),
                                                  Color.Template.randomColor() );
                if (!players.contains( randomPlayer ))
                    players.add( randomPlayer );
            }

            return new Game( levelSize, ImmutableList.copyOf( players ), resourceConfig, unitConfig );
        }

        public Size getLevelSize() {

            return levelSize;
        }

        public Builder setLevelSize(final Size levelSize) {

            this.levelSize = levelSize;

            return this;
        }

        public List<Player> getPlayers() {

            return players;
        }

        public Builder setPlayers(final List<Player> players) {

            this.players = players;

            return this;
        }

        public Integer getTotalPlayers() {

            return totalPlayers;
        }

        public void setTotalPlayers(final Integer totalPlayers) {

            this.totalPlayers = totalPlayers;
        }

        public GameResourceConfig getResourceConfig() {
            return resourceConfig;
        }

        public void setResourceConfig(final GameResourceConfig resourceConfig) {
            this.resourceConfig = resourceConfig;
        }

        public GameUnitConfig getUnitConfig() {
            return unitConfig;
        }

        public void setUnitConfig(final GameUnitConfig unitConfig) {
            this.unitConfig = unitConfig;
        }

        public int nextPlayerID() {

            return nextPlayerID++;
        }
    }


    public interface GameResourceConfig {

        int quantity(ResourceType resourceType);

        int quantityPerTile(ResourceType resourceType);

        int puddleSize(ResourceType resourceType);
    }


    public interface GameUnitConfig {

        void addUnits(Game game, Player player);
    }


    public enum GameResourceConfigs implements GameResourceConfig {
        NONE( 0, 0, 0 ),
        SCARCE( 1500, 20, 1 ),
        PLENTY( 5000, 30, 2 ),
        LOTS( 20000, 40, 5 ),
        EXCESSIVE( 1000000, 100, 5 );

        private final int quantity;
        private final int quantityPerTile;
        private final int puddleSize;

        GameResourceConfigs(final int quantity, final int quantityPerTile, final int puddleSize) {

            this.quantity = quantity;
            this.quantityPerTile = quantityPerTile;
            this.puddleSize = puddleSize;
        }

        @Override
        public int quantity(final ResourceType resourceType) {

            return quantity;
        }

        @Override
        public int quantityPerTile(final ResourceType resourceType) {
            return quantityPerTile;
        }

        @Override
        public int puddleSize(final ResourceType resourceType) {

            return puddleSize;
        }

    }


    public enum GameUnitConfigs implements GameUnitConfig {
        NONE {
            @Override
            public void addUnits(final Game game, final Player player) {
            }
        },
        BASIC {
            @Override
            public void addUnits(final Game game, final Player player) {
                // Find tiles for the units.
                Tile startTileEngineer, startTileAirship, startTileScout;
                do {
                    Level ground = game.getLevel( LevelType.GROUND );
                    startTileEngineer = ground.getTile( RANDOM.nextInt( ground.getSize().getWidth() ),
                                                        RANDOM.nextInt( ground.getSize().getHeight() ) ).get();

                    Level sky = game.getLevel( LevelType.SKY );
                    Coordinate.Side randomSide = Coordinate.Side.values()[RANDOM.nextInt( Coordinate.Side.values().length )];
                    startTileAirship = sky.getTile( startTileEngineer.neighbour( randomSide ).getPosition() ).get();

                    randomSide = Coordinate.Side.values()[RANDOM.nextInt( Coordinate.Side.values().length )];
                    startTileScout = startTileEngineer.neighbour( randomSide );
                }
                while (startTileEngineer.getContents().isPresent() || //
                       startTileAirship.getContents().isPresent() || //
                       startTileScout.getContents().isPresent());

                // Add the units.
                new GameObject( UnitTypes.ENGINEER, game, player, startTileEngineer );
                new GameObject( UnitTypes.AIRSHIP, game, player, startTileAirship );
                new GameObject( UnitTypes.SCOUT, game, player, startTileScout );
            }
        }
    }
}
