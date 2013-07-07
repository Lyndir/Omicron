package com.lyndir.omnicron.api.model;

import com.google.common.collect.*;
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

    @ObjectMeta(ignoreFor = ObjectMeta.For.all)
    private final GameController gameController = new GameController( this );

    @ObjectMeta(ignoreFor = ObjectMeta.For.toString)
    private final Collection<Player> players = new LinkedList<>();

    public Collection<Player> getPlayers() {

        return players;
    }

    public static class Builder {

        private Size         worldSize    = new Size( 50, 50 );
        private List<Player> players      = Lists.newLinkedList();
        private int          nextPlayerID = 1;
        private int          totalPlayers = 4;

        public Game build() {

            // Add random players until totalPlayers count is satisfied.
            int playerID = nextPlayerID;
            while (players.size() < totalPlayers) {
                Player randomPlayer = new Player( playerID++, Player.randomName(), Color.Template.randomColor(),
                                                  Color.Template.randomColor() );
                if (!players.contains( randomPlayer ))
                    players.add( randomPlayer );
            }

            return new Game( worldSize, players );
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

    private Game(final Size worldSize, final List<Player> players) {

        ground = new GroundLevel( worldSize );
        sky = new SkyLevel( worldSize );
        space = new SpaceLevel( worldSize );
        this.players.addAll( players );

        for (final Player player : players) {
            Tile startTile;
            do {
                startTile = ground.getTile( new Coordinate( RANDOM.nextInt( ground.getLevelSize().getWidth() ),
                                                            RANDOM.nextInt( ground.getLevelSize().getHeight() ) ) );
                assert startTile != null;
            }
            while (startTile.getContents() != null);
            player.getController().addObject( new Engineer( startTile, player ) );
        }
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
}
