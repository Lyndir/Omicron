package com.lyndir.omicron.cli.command;

import com.lyndir.omicron.cli.OmicronCLI;
import java.util.Iterator;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
@CommandGroup(name = "", abbr = "", desc = "")
public class RootCommand extends Command {

    public RootCommand(final OmicronCLI omicron) {
        super( omicron );
    }

    @Override
    public void evaluate(final Iterator<String> tokens) {

        if (!tokens.hasNext())
            // No command.
            return;

        super.evaluate( tokens );
    }

    @SubCommand(abbr = "q", desc = "Shut down the omicron CLI client.")
    public void quit(final Iterator<String> tokens) {

        getOmicron().setRunning( false );
    }
}
