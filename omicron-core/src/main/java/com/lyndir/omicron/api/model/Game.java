package com.lyndir.omicron.api.model;

import com.google.common.base.Predicate;
import com.google.common.collect.*;
import com.lyndir.lhunath.opal.system.util.MetaObject;
import com.lyndir.lhunath.opal.system.util.ObjectMeta;
import com.lyndir.omicron.api.controller.GameController;
import java.util.*;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
@SuppressWarnings("ParameterHidesMemberVariable") // IDEA doesn't understand setters that return this.
public class Game extends MetaObject {

    private static final Random RANDOM = new Random();

    private final GroundLevel ground;
    private final SkyLevel    sky;
    private final SpaceLevel  space;
    private final Turn        currentTurn;

    @ObjectMeta(ignoreFor = ObjectMeta.For.all)
    private final GameController gameController;

    private final ImmutableList<Level>  levels;
    private final ImmutableList<Player> players;
    private final Set<Player> readyPlayers = new HashSet<>();


    public static class Builder {

        private Size         worldSize    = new Size( 20, 20 );
        private List<Player> players      = Lists.newLinkedList();
        private int          nextPlayerID = 1;
        private int          totalPlayers = 4;

        public Game build() {

            // Add random players until totalPlayers count is satisfied.
            int playerID = nextPlayerID;
            while (players.size() < totalPlayers) {
                Player randomPlayer = new Player( playerID++, null, Player.randomName(), Color.Template.randomColor(),
                                                  Color.Template.randomColor() );
                if (!players.contains( randomPlayer ))
                    players.add( randomPlayer );
            }

            return new Game( worldSize, ImmutableList.copyOf( players ) );
        }

        public Size getWorldSize() {

            return worldSize;
        }

        public Builder setWorldSize(final Size worldSize) {

            this.worldSize = worldSize;

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

        public int nextPlayerID() {

            return nextPlayerID++;
        }
    }

    public static Builder builder() {

        return new Builder();
    }

    private Game(final Size worldSize, final ImmutableList<Player> players) {

        ground = new GroundLevel( worldSize, this );
        sky = new SkyLevel( worldSize, this );
        space = new SpaceLevel( worldSize, this );
        levels = ImmutableList.of( ground, sky, space );
        this.players = players;
        currentTurn = new Turn( null );

        for (final Player player : players) {
            // Find tiles for the units.
            Tile startTileEngineer, startTileAirship, startTileScout;
            do {
                startTileEngineer = ground.getTile(
                        new Coordinate( RANDOM.nextInt( ground.getSize().getWidth() ), RANDOM.nextInt( ground.getSize().getHeight() ),
                                        ground.getSize() ) ).get();

                Coordinate.Side randomSide = Coordinate.Side.values()[RANDOM.nextInt( Coordinate.Side.values().length )];
                startTileAirship = sky.getTile( startTileEngineer.neighbour( randomSide ).getPosition() ).get();

                randomSide = Coordinate.Side.values()[RANDOM.nextInt( Coordinate.Side.values().length )];
                startTileScout = startTileEngineer.neighbour( randomSide );
            }
            while (startTileEngineer.getContents().isPresent() || //
                   startTileAirship.getContents().isPresent() || //
                   startTileScout.getContents().isPresent());

            // Add the units.
            player.getController().addObject( new Engineer( startTileEngineer, player ) );
            player.getController().addObject( new Airship( startTileAirship, player ) );
            player.getController().addObject( new Scout( startTileScout, player ) );
        }

        gameController = new GameController( this );
        gameController.onNewTurn();
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

    public ImmutableList<Level> listLevels() {

        return levels;
    }

    public Collection<Player> getPlayers() {

        return players;
    }

    public Set<Player> getReadyPlayers() {

        return readyPlayers;
    }
}
