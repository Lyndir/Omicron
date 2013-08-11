package com.lyndir.omicron.api.model;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.*;
import com.lyndir.lhunath.opal.system.util.ObjectUtils;
import com.lyndir.omicron.api.Authenticated;
import com.lyndir.omicron.api.view.PlayerGameInfo;
import java.util.HashSet;
import java.util.Set;


public class GameController {

    private final Game game;
    private final Set<GameListener> gameListeners = new HashSet<>();

    GameController(final Game game) {

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
     * @param player The player whose information is being requested.
     *
     * @return Information visible to the current player about the given player.
     */
    @Authenticated
    public PlayerGameInfo getPlayerGameInfo(final Player player) {

        if (player.listObservableTiles().iterator().hasNext())
            return PlayerGameInfo.discovered( player, player.getScore() );

        return PlayerGameInfo.undiscovered( player );
    }

    @Authenticated
    public ImmutableCollection<PlayerGameInfo> listPlayerGameInfo() {

        return ImmutableList.copyOf( Lists.transform( game.getPlayers(), new Function<Player, PlayerGameInfo>() {
            @Override
            public PlayerGameInfo apply(final Player input) {

                return getPlayerGameInfo( input );
            }
        } ) );
    }

    public Iterable<Player> listPlayers() {

        return game.getPlayers();
    }

    /**
     * Indicate that the current player is ready with his turn.
     *
     * @return true if this action has caused a new turn to begin.
     */
    @Authenticated
    public boolean setReady() {
        return setReady( Security.getCurrentPlayer() );
    }

    /**
     * Indicate that the given player is ready with his turn.
     *
     * NOTE: The player must be key-less or be the currently authenticated player.
     *
     * @return true if this action has caused a new turn to begin.
     */
    boolean setReady(Player player) {
        if (!player.isKeyLess() && ObjectUtils.isEqual( player, Security.getCurrentPlayer() ))
            return false;

        game.getReadyPlayers().add( player );
        for (final GameListener gameListener : gameListeners)
            gameListener.onPlayerReady( player );

        if (game.getReadyPlayers().containsAll( game.getPlayers() )) {
            game.getReadyPlayers().clear();
            onNewTurn();
            return true;
        }

        return false;
    }

    void start() {

        Preconditions.checkState( !game.isRunning(), "The game cannot be started: It is already running." );

        game.setRunning( true );
        onNewTurn();
    }

    protected void onNewTurn() {

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

    public ImmutableSet<Player> listReadyPlayers() {

        return ImmutableSet.copyOf( game.getReadyPlayers() );
    }
}
