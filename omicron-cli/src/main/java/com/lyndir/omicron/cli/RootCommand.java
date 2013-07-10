package com.lyndir.omicron.cli;

import java.util.Iterator;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
@CommandGroup(name = "", abbr = "", desc = "")
public class RootCommand extends Command {

    @Override
    public void evaluate(final OmicronCLI omicron, final Iterator<String> tokens) {

        if (!tokens.hasNext())
            // No command.
            return;

        super.evaluate( omicron, tokens );
    }

    @SubCommand(abbr = "q", desc = "Shut down the omicron CLI client.")
    public void quit(final OmicronCLI omicron, final Iterator<String> tokens) {

        omicron.setRunning( false );
    }
}
