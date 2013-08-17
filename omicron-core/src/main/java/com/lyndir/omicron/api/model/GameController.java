package com.lyndir.omicron.api.model;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.*;
import com.lyndir.lhunath.opal.system.util.*;
import com.lyndir.omicron.api.Authenticated;
import com.lyndir.omicron.api.GameListener;
import com.lyndir.omicron.api.view.PlayerGameInfo;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;


public class GameController {

    private final Game game;
    private final Map<GameListener, Player> gameListeners = new HashMap<>();

    GameController(final Game game) {
        this.game = game;

        for (final Player player : game.getPlayers())
            player.getController().setGameController( this );
    }

    public Game getGame() {
        return game;
    }

    void addInternalGameListener(final GameListener gameListener) {
        gameListeners.put( gameListener, null );
    }

    @Authenticated
    public void addGameListener(final GameListener gameListener) {
        gameListeners.put( gameListener, Security.getCurrentPlayer() );
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
    boolean setReady(final Player player) {
        if (!player.isKeyLess())
            Preconditions.checkState( ObjectUtils.isEqual( player, Security.getCurrentPlayer() ),
                                      "Cannot set protected player ready: not authenticated.  First authenticate using Security.authenticate()." );

        game.getReadyPlayers().add( player );
        for (final GameListener gameListener : gameListeners.keySet())
            gameListener.onPlayerReady( player );

        if (game.getReadyPlayers().containsAll( game.getPlayers() )) {
            game.getReadyPlayers().clear();
            fireNewTurn();
            return true;
        }

        return false;
    }

    private void start() {
        Preconditions.checkState( !game.isRunning(), "The game cannot be started: It is already running." );

        game.setRunning( true );
        fireFor( null ).onGameStarted( game );
    }

    void end(final VictoryConditionType victoryCondition, @Nullable final Player victor) {
        Preconditions.checkState( game.isRunning(), "The game cannot end: It isn't running yet." );

        game.setRunning( false );
        fireFor( null ).onGameEnded( game, victoryCondition, victor );
    }

    private void fireNewTurn() {
        onNewTurn();

        for (final GameListener gameListener : gameListeners.keySet())
            gameListener.onNewTurn( game.getCurrentTurn() );
    }

    protected void onNewTurn() {
        game.setCurrentTurn( new Turn( game.getCurrentTurn() ) );
        if (!game.isRunning())
            start();

        for (final Player player : ImmutableList.copyOf( game.getPlayers() )) {
            player.getController().fireReset();
            player.getController().fireNewTurn();
        }
    }

    public ImmutableList<Level> listLevels() {
        return game.listLevels();
    }

    public ImmutableSet<Player> listReadyPlayers() {
        return ImmutableSet.copyOf( game.getReadyPlayers() );
    }

    /**
     * Get a game listener proxy to call an event on that should be fired.
     *
     * @param playerCondition If not null, the event is only broadcast to internal game listeners and game listeners that pass the
     *                        predicate.
     */
    GameListener fireFor(@Nullable final PredicateNN<Player> playerCondition) {
        return TypeUtils.newProxyInstance( GameListener.class, new InvocationHandler() {
            @Override
            @Nullable
            @SuppressWarnings("ProhibitedExceptionDeclared")
            public Object invoke(final Object proxy, final Method method, final Object[] args)
                    throws Throwable {
                for (final Map.Entry<GameListener, Player> gameListenerEntry : gameListeners.entrySet()) {
                    Player gameListenerOwner = gameListenerEntry.getValue();
                    if (gameListenerOwner == null || playerCondition == null || playerCondition.apply( gameListenerOwner ))
                        method.invoke( gameListenerEntry.getKey(), args );
                }

                return null;
            }
        } );
    }
}
