package com.lyndir.omicron.api;

import com.google.common.base.Preconditions;
import com.google.common.collect.*;
import com.lyndir.lhunath.opal.system.error.InternalInconsistencyException;
import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.system.util.*;
import com.lyndir.omicron.api.error.NotAuthenticatedException;
import com.lyndir.omicron.api.view.PlayerGameInfo;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class GameController implements IGameController {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger logger = Logger.get( GameController.class );

    private final Game game;
    private final Map<GameListener, Player> gameListeners = Collections.synchronizedMap( Maps.<GameListener, Player>newLinkedHashMap() );

    GameController(final Game game) {
        this.game = game;

        for (final Player player : game.getPlayers())
            player.getController().setGameController( this );
    }

    @Override
    public int hashCode() {
        return game.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof IGameController && Objects.equals( game, ((IGameController) obj).getGame() );
    }

    @Override
    public Game getGame() {
        return game;
    }

    void addInternalGameListener(final GameListener gameListener) {
        gameListeners.put( gameListener, null );
    }

    void addGameListeners(final Map<GameListener, Player> newGameListeners) {
        gameListeners.putAll( newGameListeners );
    }

    @Override
    public void addGameListener(final GameListener gameListener)
            throws NotAuthenticatedException {
        gameListeners.put( gameListener, Security.currentPlayer() );
    }

    /**
     * Retrieve information on a given player.
     *
     * @param player The player whose information is being requested.
     *
     * @return Information visible to the current player about the given player.
     */
    @Override
    public PlayerGameInfo getPlayerGameInfo(final IPlayer player)
            throws NotAuthenticatedException {
        if (player.observableTiles().iterator().hasNext())
            return PlayerGameInfo.discovered( player, player.getScore() );

        return PlayerGameInfo.undiscovered( player );
    }

    @Override
    public ImmutableCollection<PlayerGameInfo> listPlayerGameInfo()
            throws NotAuthenticatedException {
        ImmutableList.Builder<PlayerGameInfo> playerGameInfoBuilder = ImmutableList.builder();
        for (final IPlayer player : game.getPlayers())
            playerGameInfoBuilder.add( getPlayerGameInfo( player ) );

        return playerGameInfoBuilder.build();
    }

    /**
     * Indicate that the current player is ready with his turn.
     *
     * @return true if this action has caused a new turn to begin.
     */
    @Override
    public boolean setReady()
            throws NotAuthenticatedException {
        return setReady( Player.cast( Security.currentPlayer() ) );
    }

    /**
     * Indicate that the given player is ready with his turn.
     *
     * NOTE: The player must be key-less or be the currently authenticated player.
     *
     * @return true if this action has caused a new turn to begin.
     */
    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    boolean setReady(final Player player)
            throws NotAuthenticatedException {
        if (!player.isKeyLess())
            Preconditions.checkState( ObjectUtils.isEqual( player, Security.currentPlayer() ),
                                      "Cannot set protected player ready: not authenticated.  First authenticate using Security.authenticate()." );

        boolean allReady = game.setReady( player );
        fire().onPlayerReady( player );

        if (allReady)
            Security.godRun( this::fireNewTurn );

        return allReady;
    }

    private void start() {
        Preconditions.checkState( !game.isRunning(), "The game cannot be started: It is already running." );

        game.setRunning( true );
        fire().onGameStarted( game );
    }

    void end(final VictoryConditionType victoryCondition, @Nullable final IPlayer victor) {
        Preconditions.checkState( game.isRunning(), "The game cannot end: It isn't running yet." );

        game.setRunning( false );
        fire().onGameEnded( game, victoryCondition.pub(), victor );
    }

    private void fireNewTurn() {
        onNewTurn();

        fire().onNewTurn( game.getTurns().getLast() );
    }

    protected void onNewTurn() {
        game.newTurn();
        if (!game.isRunning())
            start();

        for (final Player player : game.getPlayers())
            Security.playerRun( player, () -> {
                player.getController().fireReset();
                player.getController().fireNewTurn();
            } );
    }

    /**
     * Get a game listener proxy to call an event on that should be fired for all game listeners.
     */
    GameListener fire() {
        return TypeUtils.newProxyInstance( GameListener.class, (proxy, method, args) -> {
            synchronized (gameListeners) {
                if (method.getDeclaringClass() == Object.class)
                    return Void.TYPE;

                logger.dbg( "%s: %s", method.getName(), ObjectUtils.describe( args ) );
                for (final Map.Entry<GameListener, Player> gameListenerEntry : gameListeners.entrySet()) {
                    Player gameListenerOwner = gameListenerEntry.getValue();
                    if (gameListenerOwner == null)
                        Security.godRun( newGameListenerJob( gameListenerEntry.getKey(), method, args ) );
                    else
                        Security.playerRun( gameListenerOwner, newGameListenerJob( gameListenerEntry.getKey(), method, args ) );
                }

                return Void.TYPE;
            }
        } );
    }

    /**
     * Get a game listener proxy to call an event that should be fired for all game listeners that are either internal or registered by
     * players that pass the playerCondition.
     *
     * @param playerCondition The predicate that should hold true for all players eligible to receive the notification.
     */
    GameListener fireIfPlayer(@Nonnull final PredicateNN<IPlayer> playerCondition) {
        return TypeUtils.newProxyInstance( GameListener.class, (proxy, method, args) -> {
            synchronized (gameListeners) {
                if (method.getDeclaringClass() == Object.class)
                    return Void.TYPE;

                logger.dbg( "%s: %s", method.getName(), ObjectUtils.describe( args ) );
                for (final Map.Entry<GameListener, Player> gameListenerEntry : gameListeners.entrySet()) {
                    Player gameListenerOwner = gameListenerEntry.getValue();
                    if (gameListenerOwner == null)
                        Security.godRun( newGameListenerJob( gameListenerEntry.getKey(), method, args ) );
                    else if (playerCondition.apply( gameListenerOwner ))
                        Security.playerRun( gameListenerOwner, newGameListenerJob( gameListenerEntry.getKey(), method, args ) );
                }

                return Void.TYPE;
            }
        } );
    }

    /**
     * Get a game listener proxy to call an event on that should be fired for all game listeners that are either internal or registered by
     * players that can observe the given location.
     *
     * @param location The location that should be observable.
     */
    GameListener fireIfObservable(@Nonnull final ITile location) {
        return fireIfPlayer( player -> Security.godRun( () -> player.canObserve( location ).isTrue() ) );
    }

    /**
     * Get a game listener proxy to call an event on that should be fired for all game listeners that are either internal or registered by
     * players that can observe the given object.
     *
     * @param gameObject The game object that should be observable.
     */
    GameListener fireIfObservable(@Nonnull final IGameObject gameObject) {
        return fireIfPlayer( player -> Security.godRun( () -> player.canObserve( gameObject ).isTrue() ) );
    }

    private static Runnable newGameListenerJob(final GameListener gameListener, final Method method, final Object[] args) {
        return () -> {
            try {
                //noinspection unchecked
                if (method.getDeclaringClass() == GameListener.class)
                    method.invoke( gameListener, args );
            }
            catch (IllegalAccessException | InvocationTargetException e) {
                throw new InternalInconsistencyException( "Fix: " + gameListener, e );
            }
        };
    }
}
