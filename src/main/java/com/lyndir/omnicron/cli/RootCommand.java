package com.lyndir.omnicron.cli;

import java.util.Iterator;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
@CommandGroup(name = "", abbr = "", desc = "")
public class RootCommand extends Command {

    @Override
    public void evaluate(final OmnicronCLI omnicron, final Iterator<String> tokens) {

        if (!tokens.hasNext())
            // No command.
            return;

        super.evaluate( omnicron, tokens );
    }

    @SubCommand(description = "Shut down the omnicron CLI client.")
    public void exit(final OmnicronCLI omnicron, final Iterator<String> tokens) {

        omnicron.setRunning( false );
    }
}
