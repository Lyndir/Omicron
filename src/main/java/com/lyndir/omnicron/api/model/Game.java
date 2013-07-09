package com.lyndir.omnicron.api.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.lyndir.lhunath.opal.system.util.MetaObject;
import com.lyndir.lhunath.opal.system.util.ObjectMeta;
import com.lyndir.omnicron.api.controller.GameController;
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

        ground = new GroundLevel( worldSize );
        sky = new SkyLevel( worldSize );
        space = new SpaceLevel( worldSize );
        levels = ImmutableList.of( ground, sky, space );
        this.players = players;
        currentTurn = new Turn( null );

        for (final Player player : players) {
            Tile startTile, startTile2;
            do {
                startTile = ground.getTile( new Coordinate( RANDOM.nextInt( ground.getLevelSize().getWidth() ),
                                                            RANDOM.nextInt( ground.getLevelSize().getHeight() ), ground.getLevelSize() ) );
                assert startTile != null;
            }
            while (startTile.getContents() != null);
            do {
                startTile2 = sky.getTile(
                        new Coordinate( RANDOM.nextInt( sky.getLevelSize().getWidth() ), RANDOM.nextInt( sky.getLevelSize().getHeight() ),
                                        sky.getLevelSize() ) );
                assert startTile2 != null;
            }
            while (startTile2.getContents() != null);
            player.getController().addObject( new Engineer( startTile, player ) );
            player.getController().addObject( new Airship( startTile2, player ) );
        }

        gameController = new GameController( this );
        gameController.newTurn();
    }

    public GameController getController() {

        return gameController;
    }

    public GroundLevel getGround() {

        return ground;
    }

    public SkyLevel getSky() {

        return sky;
    }

    public SpaceLevel getSpace() {

        return space;
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
