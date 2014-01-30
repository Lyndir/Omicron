package com.lyndir.omicron.cli;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.google.common.base.*;
import com.lyndir.lanterna.view.OmicronWindow;
import com.lyndir.omicron.api.model.*;
import com.lyndir.omicron.api.GameListener;
import java.util.*;
import javax.annotation.Nonnull;
import org.slf4j.LoggerFactory;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
public class OmicronCLI {

    private static final OmicronCLI omicron = new OmicronCLI();

    private final Builders          builders      = new Builders();
    private final PlayerKey         localKey      = new PlayerKey();
    private final List<String>      log           = new LinkedList<>();
    private final Set<GameListener> gameListeners = new HashSet<>();
    private       boolean           running       = true;
    private IGameController gameController;
    private IPlayer         localPlayer;

    @SuppressWarnings("ProhibitedExceptionDeclared")
    public static void main(final String... arguments)
            throws Exception {
        // Attach the omicron command log appender to logback.
        LoggerContext logbackFactory = (LoggerContext) LoggerFactory.getILoggerFactory();
        OmicronCLIAppender newAppender = new OmicronCLIAppender();
        newAppender.setContext( logbackFactory );
        Logger logger = logbackFactory.getLogger( Logger.ROOT_LOGGER_NAME );
        logger.addAppender( newAppender );
        newAppender.start();

        new OmicronWindow().start();
    }

    private OmicronCLI() {
    }

    public static OmicronCLI get() {
        return omicron;
    }

    public boolean isRunning() {

        return running;
    }

    public void setRunning(final boolean running) {

        this.running = running;
    }

    public Optional<IGameController> getGameController() {

        return Optional.fromNullable( gameController );
    }

    public void setGameController(@Nonnull final IGameController gameController) {

        Preconditions.checkState( this.gameController == null, "Cannot assign a new game controller, one has already been assigned." );
        this.gameController = gameController;

        for (final GameListener gameListener : gameListeners)
            gameController.addGameListener( gameListener );
    }

    public Optional<IPlayer> getLocalPlayer() {

        return Optional.fromNullable( localPlayer );
    }

    public void setLocalPlayer(final IPlayer localPlayer) {

        Preconditions.checkState( this.localPlayer == null, "Cannot assign a new local player, one has already been assigned." );
        this.localPlayer = localPlayer;
    }

    public PlayerKey getLocalKey() {

        return localKey;
    }

    public List<String> getLog() {
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
}
