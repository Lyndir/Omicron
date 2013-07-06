package com.lyndir.omnicron.cli;

import java.util.Iterator;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
@CommandGroup(name = "add")
public class AddCommand extends Command {

    @SubCommand(description = "Add things to an Omnicron game that is being built.")
    public void game(final OmnicronCLI omnicron, final Iterator<String> tokens) {

        new AddGameCommand().evaluate( omnicron, tokens );
    }
}
