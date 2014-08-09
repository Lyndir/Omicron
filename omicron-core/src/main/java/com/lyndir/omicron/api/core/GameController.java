package com.lyndir.omicron.api.core;

import static com.lyndir.omicron.api.core.CoreUtils.*;

import com.google.common.base.Preconditions;
import com.google.common.collect.*;
import com.lyndir.lhunath.opal.system.error.InternalInconsistencyException;
import com.lyndir.lhunath.opal.system.util.*;
import com.lyndir.omicron.api.Authenticated;
import com.lyndir.omicron.api.GameListener;
import com.lyndir.omicron.api.view.PlayerGameInfo;
import java.lang.reflect.*;
import java.util.*;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class GameController implements IGameController {

    private final Game game;
    private final Map<GameListener, IPlayer> gameListeners = Collections.synchronizedMap( Maps.<GameListener, IPlayer>newLinkedHashMap() );

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

    void addGameListeners(final Map<GameListener, IPlayer> gameListeners) {
        this.gameListeners.putAll( gameListeners );
    }

    @Override
    @Authenticated
    public void addGameListener(final GameListener gameListener)
            throws Security.NotAuthenticatedException {
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
    @Authenticated
    public PlayerGameInfo getPlayerGameInfo(final IPlayer player)
            throws Security.NotAuthenticatedException {
        if (player.iterateObservableTiles().iterator().hasNext())
            return PlayerGameInfo.discovered( player, player.getScore() );

        return PlayerGameInfo.undiscovered( player );
    }

    @Override
    @Authenticated
    public ImmutableCollection<PlayerGameInfo> listPlayerGameInfo()
            throws Security.NotAuthenticatedException {
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
    @Authenticated
    public boolean setReady()
            throws Security.NotAuthenticatedException {
        return setReady( coreP( Security.currentPlayer() ) );
    }

    /**
     * Indicate that the given player is ready with his turn.
     *
     * NOTE: The player must be key-less or be the currently authenticated player.
     *
     * @return true if this action has caused a new turn to begin.
     */
    boolean setReady(final Player player)
            throws Security.NotAuthenticatedException {
        if (!player.isKeyLess())
            Preconditions.checkState( ObjectUtils.isEqual( player, Security.currentPlayer() ),
                                      "Cannot set protected player ready: not authenticated.  First authenticate using Security.authenticate()." );

        Set<Player> readyPlayers = game.getReadyPlayers();
        synchronized (readyPlayers) {
            readyPlayers.add( player );
            fire().onPlayerReady( player );

            if (readyPlayers.containsAll( game.getPlayers() )) {
                readyPlayers.clear();
                Security.godRun( new RunnableJob<Void>() {
                    @Override
                    public void run() {
                        fireNewTurn();
                    }
                } );
                return true;
            }
        }

        return false;
    }

    private void start() {
        Preconditions.checkState( !game.isRunning(), "The game cannot be started: It is already running." );

        game.setRunning( true );
        fire().onGameStarted( game );
    }

    void end(final VictoryConditionType victoryCondition, @Nullable final IPlayer victor) {
        Preconditions.checkState( game.isRunning(), "The game cannot end: It isn't running yet." );

        game.setRunning( false );
        fire().onGameEnded( game, publicVCT( victoryCondition ), victor );
    }

    private void fireNewTurn() {
        onNewTurn();

        fire().onNewTurn( game.getTurns() );
    }

    protected void onNewTurn() {
        game.setCurrentTurn( new Turn( game.getTurns() ) );
        if (!game.isRunning())
            start();

        for (final Player player : game.getPlayers())
            Security.playerRun( player, new Runnable() {
                @Override
                public void run() {
                    player.getController().fireReset();
                    player.getController().fireNewTurn();
                }
            } );
    }

    /**
     * Get a game listener proxy to call an event on that should be fired for all game listeners.
     */
    GameListener fire() {
        return TypeUtils.newProxyInstance( GameListener.class, new InvocationHandler() {
            @Nullable
            @Override
            public Object invoke(final Object proxy, final Method method, final Object[] args) {
                synchronized (gameListeners) {
                    for (final Map.Entry<GameListener, IPlayer> gameListenerEntry : gameListeners.entrySet()) {
                        IPlayer gameListenerOwner = gameListenerEntry.getValue();
                        if (gameListenerOwner == null)
                            Security.godRun( newGameListenerJob( gameListenerEntry.getKey(), method, args ) );
                        else
                            Security.playerRun( gameListenerOwner, newGameListenerJob( gameListenerEntry.getKey(), method, args ) );
                    }
                }

                return null;
            }
        } );
    }

    /**
     * Get a game listener proxy to call an event that should be fired for all game listeners that are either internal or registered by
     * players that pass the playerCondition.
     *
     * @param playerCondition The predicate that should hold true for all players eligible to receive the notification.
     */
    GameListener fireIfPlayer(@Nonnull final PredicateNN<Player> playerCondition) {
        return TypeUtils.newProxyInstance( GameListener.class, new InvocationHandler() {
            @Override
            @Nullable
            @SuppressWarnings("ProhibitedExceptionDeclared")
            public Object invoke(final Object proxy, final Method method, final Object[] args)
                    throws Throwable {
                synchronized (gameListeners) {
                    for (final Map.Entry<GameListener, IPlayer> gameListenerEntry : gameListeners.entrySet()) {
                        Player gameListenerOwner = corePN( gameListenerEntry.getValue() );
                        if (gameListenerOwner == null)
                            Security.godRun( newGameListenerJob( gameListenerEntry.getKey(), method, args ) );
                        else if (playerCondition.apply( gameListenerOwner ))
                            Security.playerRun( gameListenerOwner, newGameListenerJob( gameListenerEntry.getKey(), method, args ) );
                    }
                }

                return null;
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
        return fireIfPlayer( new PredicateNN<Player>() {
            @Override
            public boolean apply(@Nonnull final Player player) {
                return Security.godRun( new Job<Boolean>() {
                    @Override
                    public Boolean execute() {
                        return player.canObserve( location ).isTrue();
                    }
                } );
            }
        } );
    }

    /**
     * Get a game listener proxy to call an event on that should be fired for all game listeners that are either internal or registered by
     * players that can observe the given object.
     *
     * @param gameObject The game object that should be observable.
     */
    GameListener fireIfObservable(@Nonnull final IGameObject gameObject) {
        return fireIfPlayer( new PredicateNN<Player>() {
            @Override
            public boolean apply(@Nonnull final Player player) {
                return Security.godRun( new Job<Boolean>() {
                    @Override
                    public Boolean execute() {
                        return player.canObserve( gameObject ).isTrue();
                    }
                } );
            }
        } );
    }

    private static RunnableJob<Void> newGameListenerJob(final GameListener gameListener, final Method method, final Object[] args) {
        return new RunnableJob<Void>() {
            @Override
            public void run() {
                try {
                    //noinspection unchecked
                    if (method.getDeclaringClass() == GameListener.class)
                        method.invoke( gameListener, args );
                }
                catch (IllegalAccessException | InvocationTargetException e) {
                    throw new InternalInconsistencyException( "Fix: " + gameListener, e );
                }
            }
        };
    }
}
