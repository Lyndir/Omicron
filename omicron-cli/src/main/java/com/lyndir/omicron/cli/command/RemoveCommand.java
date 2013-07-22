package com.lyndir.omicron.cli.command;

import com.lyndir.omicron.cli.OmicronCLI;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
@CommandGroup(name = "remove", abbr = "rm", desc = "Remove objects from properties of a built object.")
public class RemoveCommand extends Command {

    public RemoveCommand(final OmicronCLI omicron) {
        super( omicron );
    }
}
