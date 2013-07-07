package com.lyndir.omnicron.cli;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.io.LineReader;
import com.lyndir.omnicron.api.controller.GameController;
import com.lyndir.omnicron.api.model.Game;
import com.lyndir.omnicron.api.model.Player;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.regex.Pattern;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
public class OmnicronCLI {

    private static final Splitter commandSplitter = Splitter.on( Pattern.compile( "\\s+" ) ).omitEmptyStrings().trimResults();

    private final Builders builders = new Builders();
    private       boolean  running  = true;

    private GameController gameController;
    private Player         localPlayer;

    @SuppressWarnings("ProhibitedExceptionDeclared")
    public static void main(final String... arguments)
            throws Exception {

        try (InputStreamReader inReader = new InputStreamReader( System.in, Charsets.UTF_8 )) {
            OmnicronCLI omnicron = new OmnicronCLI();
            LineReader inLineReader = new LineReader( inReader );

            System.err.println( "Welcome to Omnicron." );
            System.err.println( "Issue your commands." );
            System.err.println( "====================" );
            new BuildCommand().game( omnicron, ImmutableList.<String>of().iterator() );
            new AddGameCommand().player( omnicron, ImmutableList.of("Simon,red,red").iterator() );
            new CreateCommand().game( omnicron, ImmutableList.<String>of().iterator() );
            while (omnicron.isRunning()) {
                System.err.print( "% " );
                Iterator<String> tokens = commandSplitter.split( inLineReader.readLine() ).iterator();

                try {
                    new RootCommand().evaluate( omnicron, tokens );
                }
                catch (RuntimeException e) {
                    System.err.format( "Unexpected: %s\n", e.getLocalizedMessage() );
                }
            }
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

    public Builders getBuilders() {

        return builders;
    }
}
