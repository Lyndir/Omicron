package com.lyndir.omicron.cli.command;

import com.lyndir.omicron.cli.OmicronCLI;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
@CommandGroup(name = "set", abbr = "s", desc = "Set properties on a built object.")
public class SetCommand extends Command {

    public SetCommand(final OmicronCLI omicron) {
        super( omicron );
    }
}
