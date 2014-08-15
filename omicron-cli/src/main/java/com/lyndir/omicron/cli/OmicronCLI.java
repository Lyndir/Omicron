package com.lyndir.omicron.cli;

import com.google.common.base.Preconditions;
import com.lyndir.lanterna.view.OmicronWindow;
import com.lyndir.omicron.api.*;
import java.util.*;
import javax.annotation.Nonnull;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
public class OmicronCLI {

    private static final OmicronCLI omicron = new OmicronCLI();

    private final Builders                 builders      = new Builders();
    private final PlayerKey                localKey      = new PlayerKey();
    private final List<String>             log           = new LinkedList<>();
    private final Collection<GameListener> gameListeners = new HashSet<>();
    private final OmicronWindow            window        = new OmicronWindow();
    private IGameController gameController;
    private IPlayer         localPlayer;
    private boolean         running;

    @SuppressWarnings("ProhibitedExceptionDeclared")
    public static void main(final String... arguments) {
        new OmicronCLIAppender().start();
        omicron.start();
    }

    public static OmicronCLI get() {
        return omicron;
    }

    private OmicronCLI() {
    }

    private void start() {
        Preconditions.checkState( !isRunning(), "This omicron CLI is already running." );

        setRunning( true );
        window.start();
    }

    public OmicronWindow getWindow() {
        return window;
    }

    public Optional<IGameController> getGameController() {

        return Optional.ofNullable( gameController );
    }

    public void setGameController(@Nonnull final IGameController gameController) {

        Preconditions.checkState( this.gameController == null, "Cannot assign a new game controller, one has already been assigned." );
        this.gameController = gameController;

        for (final GameListener gameListener : gameListeners)
            gameController.addGameListener( gameListener );
    }

    public Optional<IPlayer> getLocalPlayer() {

        return Optional.ofNullable( localPlayer );
    }

    public void setLocalPlayer(final IPlayer localPlayer) {

        Preconditions.checkState( this.localPlayer == null, "Cannot assign a new local player, one has already been assigned." );
        this.localPlayer = localPlayer;
    }

    public PlayerKey getLocalKey() {

        return localKey;
    }

    public Collection<String> getLog() {
        return log;
    }

    public Builders getBuilders() {

        return builders;
    }

    public void addGameListener(final GameListener gameListener) {
        gameListeners.add( gameListener );

        if (gameController != null)
            gameController.addGameListener( gameListener );
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(final boolean running) {
        this.running = running;
    }
}
