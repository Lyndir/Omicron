package com.lyndir.omicron.cli;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.screen.Screen;
import com.lyndir.omicron.api.controller.GameController;
import com.lyndir.omicron.api.model.Player;
import com.lyndir.omicron.api.model.PlayerKey;
import com.lyndir.omicron.cli.command.*;
import com.lyndir.omicron.cli.view.MainWindow;
import com.lyndir.omicron.cli.view.View;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
public class OmicronCLI {

    private static final ThreadLocal<OmicronCLI> omicron  = new ThreadLocal<>();
    private final        Builders                builders = new Builders();
    private final        PlayerKey               localKey = new PlayerKey();
    private final        List<String>            log      = new LinkedList<>();
    private              boolean                 running  = true;
    private GameController gameController;
    private Player         localPlayer;

    @SuppressWarnings("ProhibitedExceptionDeclared")
    public static void main(final String... arguments)
            throws Exception {

        Screen screen = new Screen( TerminalFacade.createTextTerminal() );
        screen.startScreen();
        screen.getTerminal().setCursorVisible( false );
        try {
            OmicronCLI omicron = new OmicronCLI();
            new BuildCommand(omicron).game( ImmutableList.<String>of().iterator() );
            new AddGameCommand(omicron).player( ImmutableList.of( "Simon,red,red" ).iterator() );
            new CreateCommand(omicron).game( ImmutableList.<String>of().iterator() );
            View ui = new MainWindow();

            while (omicron.isRunning()) {
                ui.measure( screen );
                ui.draw( screen );
                screen.refresh();

                // Check for input.
                for (Key key; (key = screen.readInput()) != null; )
                    ui.handleKey( key );
            }
        }
        finally {
            screen.getTerminal().setCursorVisible( true );
            screen.stopScreen();
        }
    }

    public OmicronCLI() {
        omicron.set( this );
    }

    public static OmicronCLI get() {
        return omicron.get();
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

        this.gameController = gameController;
    }

    public Player getLocalPlayer() {

        return localPlayer;
    }

    public void setLocalPlayer(final Player localPlayer) {

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
}
