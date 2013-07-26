package com.lyndir.omicron.cli;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.lyndir.lanterna.view.OmicronWindow;
import com.lyndir.omicron.api.controller.GameController;
import com.lyndir.omicron.api.controller.GameListener;
import com.lyndir.omicron.api.model.Player;
import com.lyndir.omicron.api.model.PlayerKey;
import java.util.*;
import javax.annotation.Nonnull;


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
    private GameController gameController;
    private Player         localPlayer;

    @SuppressWarnings("ProhibitedExceptionDeclared")
    public static void main(final String... arguments)
            throws Exception {

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

    public Optional<GameController> getGameController() {

        return Optional.fromNullable( gameController );
    }

    public void setGameController(@Nonnull final GameController gameController) {

        Preconditions.checkState( this.gameController == null, "Cannot assign a new game controller, one has already been assigned." );
        this.gameController = gameController;

        for (final GameListener gameListener : gameListeners)
            gameController.addGameListener( gameListener );
    }

    public Optional<Player> getLocalPlayer() {

        return Optional.fromNullable( localPlayer );
    }

    public void setLocalPlayer(final Player localPlayer) {

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
