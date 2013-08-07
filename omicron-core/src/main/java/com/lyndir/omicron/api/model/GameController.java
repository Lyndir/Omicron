package com.lyndir.omicron.api.model;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.lyndir.omicron.api.view.PlayerGameInfo;
import java.util.*;


public class GameController {

    private final Game game;
    private final Set<GameListener> gameListeners = new HashSet<>();

    public GameController(final Game game) {

        this.game = game;

        for (final Player player : game.getPlayers())
            player.getController().setGameController( this );
    }

    public Game getGame() {

        return game;
    }

    public void addGameListener(final GameListener gameListener) {
        gameListeners.add( gameListener );
    }

    /**
     * Retrieve information on a given player.
     *
     * @param currentPlayer The player requesting the information.
     * @param player        The player whose information is being requested.
     *
     * @return Information visible to the current player about the given player.
     */
    public PlayerGameInfo getPlayerGameInfo(final Player currentPlayer, final Player player) {

        if (player.listObservableTiles( currentPlayer ).iterator().hasNext())
            return PlayerGameInfo.discovered( player, player.getScore() );

        return PlayerGameInfo.undiscovered( player );
    }

    public Collection<PlayerGameInfo> listPlayerGameInfo(final Player currentPlayer) {

        return Collections2.transform( game.getPlayers(), new Function<Player, PlayerGameInfo>() {
            @Override
            public PlayerGameInfo apply(final Player input) {

                return getPlayerGameInfo( currentPlayer, input );
            }
        } );
    }

    public Iterable<Player> listPlayers() {

        return game.getPlayers();
    }

    /**
     * Indicate that the current player is ready with his turn.
     *
     * @param currentPlayer The current player.
     *
     * @return true if this action has caused a new turn to begin.
     */
    public boolean setReady(final Player currentPlayer) {

        game.getReadyPlayers().add( currentPlayer );
        for (final GameListener gameListener : gameListeners)
            gameListener.onPlayerReady( currentPlayer );

        if (game.getReadyPlayers().containsAll( game.getPlayers() )) {
            game.getReadyPlayers().clear();
            onNewTurn();
            return true;
        }

        return false;
    }

    public void start() {

        Preconditions.checkState( !game.isRunning(), "The game cannot be started: It is already running." );

        game.setRunning( true );
        onNewTurn();
    }

    public void onNewTurn() {

        game.setCurrentTurn( new Turn( game.getCurrentTurn() ) );
        for (final GameListener gameListener : gameListeners)
            gameListener.onNewTurn( game.getCurrentTurn() );

        for (final Player player : ImmutableList.copyOf( game.getPlayers() )) {
            player.getController().onReset();
            player.getController().onNewTurn();
        }
    }

    public ImmutableList<Level> listLevels() {

        return game.listLevels();
    }

    public Set<Player> listReadyPlayers() {

        return game.getReadyPlayers();
    }
}
