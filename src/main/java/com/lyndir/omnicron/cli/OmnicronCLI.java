package com.lyndir.omnicron.cli;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.io.LineReader;
import com.lyndir.omnicron.api.Game;
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

    private boolean running = true;
    private Game game;

    @SuppressWarnings("ProhibitedExceptionDeclared")
    public static void main(final String... arguments)
            throws Exception {

        InputStreamReader inReader = new InputStreamReader( System.in, Charsets.UTF_8 );
        try {
            OmnicronCLI omnicron = new OmnicronCLI();
            LineReader inLineReader = new LineReader( inReader );

            System.err.println("Welcome to Omnicron.");
            System.err.println("Issue your commands.");
            System.err.println("====================");
            while (omnicron.isRunning()) {
                System.err.print( "% " );
                Iterator<String> tokens = commandSplitter.split( inLineReader.readLine() ).iterator();

                try {
                new RootCommand().evaluate( omnicron, tokens );
                } catch (RuntimeException e) {
                    System.err.format( "Unexpected: %s\n", e.getLocalizedMessage() );
                }
            }
        }
        finally {
            inReader.close();
        }
    }

    public boolean isRunning() {

        return running;
    }

    public void setRunning(final boolean running) {

        this.running = running;
    }

    public Game getGame() {

        return game;
    }

    public void setGame(final Game game) {

        this.game = game;
    }
}
