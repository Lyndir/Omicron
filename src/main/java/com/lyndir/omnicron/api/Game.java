package com.lyndir.omnicron.api;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.*;
import com.lyndir.lhunath.opal.system.util.MetaObject;
import java.util.*;
import org.jetbrains.annotations.NotNull;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
@SuppressWarnings("ParameterHidesMemberVariable") // IDEA doesn't understand setters that return this.
public class Game extends MetaObject {

    private static Game currentGame;

    private final GroundLevel ground;
    private final SkyLevel    sky;
    private final SpaceLevel  space;

    private final Map<Player, Set<GameObject>> playerObjects;
    private final Map<Player, Integer>         playerScores;

    @NotNull
    public static Game get() {

        return Preconditions.checkNotNull( currentGame, "No game is running." );
    }

    public static void set(@NotNull final Game currentGame) {

        Game.currentGame = currentGame;
    }

    public static void unset() {

        currentGame = null;
    }

    public PlayerGameInfo getPlayerGameInfo(final GameObserver observer, final Player player) {

        if (hasDiscovered( observer, player ))
            return PlayerGameInfo.discovered( player, playerScores.get( player ) );

        return PlayerGameInfo.undiscovered( player );
    }

    private static boolean hasDiscovered(final GameObserver observer, final GameObserver target) {

        for (final Tile tile : observer.getObservedTiles())
            if (tile.contains( target ))
                return true;

        return false;
    }

    public Set<Player> getPlayers() {

        return playerScores.keySet();
    }

    public static class Builder {

        private Size         worldSize    = new Size( 100, 100 );
        private List<Player> players      = Lists.newLinkedList();
        private Integer      totalPlayers = 4;

        public Game build() {

            // Add random players until totalPlayers count is satisfied.
            while (players.size() < totalPlayers) {
                Player randomPlayer = new Player( Player.randomName(), Color.Template.randomColor(), Color.Template.randomColor() );
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
    }

    public static Builder builder() {

        return new Builder();
    }

    public Game(final Size worldSize, final List<Player> players) {

        ground = new GroundLevel( worldSize );
        sky = new SkyLevel( worldSize );
        space = new SpaceLevel( worldSize );
        playerObjects = Maps.newHashMap();
        playerScores = Maps.newHashMap();

        for (final Player player : players) {
            playerObjects.put( player, Sets.<GameObject>newHashSet() );
            playerScores.put( player, 0 );
        }
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

    public Set<GameObject> getGameObjectsForPlayer(final GameObserver observer, final Player player) {

        return Sets.filter( playerObjects.get( player ), new Predicate<GameObject>() {
            @Override
            public boolean apply(final GameObject input) {

                return observer.getObservedTiles().contains( input.getLocationTile() );
            }
        } );
    }
}
