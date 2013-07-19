package com.lyndir.omicron.cli;

import com.google.common.base.Splitter;
import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalSize;
import com.lyndir.lhunath.opal.system.util.StringUtils;
import com.lyndir.omicron.api.controller.GameController;
import com.lyndir.omicron.api.model.Player;
import com.lyndir.omicron.api.model.PlayerKey;
import java.util.regex.Pattern;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
public class OmicronCLI {

    private static final Splitter commandSplitter    = Splitter.on( Pattern.compile( "\\s+" ) ).omitEmptyStrings().trimResults();
    private static final int      COMMAND_LOG_HEIGHT = 5;

    private final Builders  builders = new Builders();
    private final PlayerKey localKey = new PlayerKey();
    private final Screen    screen   = new Screen( TerminalFacade.createTextTerminal() );
    private       boolean   running  = true;
    private GameController gameController;
    private Player         localPlayer;

    @SuppressWarnings("ProhibitedExceptionDeclared")
    public static void main(final String... arguments)
            throws Exception {

        OmicronCLI omicron = new OmicronCLI();
        Screen screen = omicron.getScreen();

        screen.startScreen();
        screen.getTerminal().setCursorVisible( false );
        try {
            while (omicron.isRunning()) {
                TerminalSize terminalSize = screen.getTerminalSize();
                String rowString = StringUtils.repeat( " ", terminalSize.getColumns() );

                // Draw the UI.
                // - Background
                for (int y = 0; y < terminalSize.getRows(); ++y)
                    screen.putString( 0, y, rowString, Terminal.Color.BLACK, Terminal.Color.DEFAULT );
                // - Title
                String title = omicron.getGameController() == null? "No game.": omicron.getGameController().getGame().toString();
                screen.putString( 0, 0, rowString, //
                                  Terminal.Color.DEFAULT, Terminal.Color.BLACK );
                screen.putString( 2, 0, String.format( "Omicron - %s", title ), //
                                  Terminal.Color.DEFAULT, Terminal.Color.BLACK );
                // - Command Log
                screen.putString( 0, terminalSize.getRows() - COMMAND_LOG_HEIGHT - 1, rowString, //
                                  Terminal.Color.DEFAULT, Terminal.Color.BLACK );
                screen.putString( 2, terminalSize.getRows() - COMMAND_LOG_HEIGHT - 1, "Command Log", //
                                  Terminal.Color.DEFAULT, Terminal.Color.BLACK );
                // - Flush.
                screen.refresh();

                // Check for input.
                Key key = screen.readInput();
                if (key != null) {
                    if (key.getCharacter() == 'q')
                        omicron.setRunning( false );
                }
            }
        }
        finally {
            screen.getTerminal().setCursorVisible( true );
            screen.stopScreen();
        }
    }

    public boolean isRunning() {

        return running;
    }

    public void setRunning(final boolean running) {

        this.running = running;
    }

    public GameController getGameController() {

        return gameController;
    }

    public void setGameController(final GameController gameController) {

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

    public Screen getScreen() {
        return screen;
    }

    public Builders getBuilders() {

        return builders;
    }
}
