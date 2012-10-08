package com.lyndir.omnicron.cli;

import java.util.Iterator;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
public class RootCommand implements Command {

    @Override
    public void evaluate(final OmnicronCLI omnicron, final Iterator<String> tokens) {

        if (!tokens.hasNext())
            return;

        String rootCommand = tokens.next();
        if ("new".equals( rootCommand ))
            new NewCommand().evaluate( omnicron, tokens );
        else if ("list".equals( rootCommand ))
            new ListCommand().evaluate( omnicron, tokens );
        else if ("exit".equals( rootCommand ))
            omnicron.setRunning( false );
        else
            System.err.format("Unknown command: %s\n", rootCommand);
    }
}
