package com.lyndir.omnicron.cli;

import java.util.Iterator;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
@CommandGroup(name = "rm")
public class RemoveCommand extends Command {

    @SubCommand(description = "Remove things from an Omnicron game that is being built.")
    public void game(final OmnicronCLI omnicron, final Iterator<String> tokens) {

        new RemoveGameCommand().evaluate( omnicron, tokens );
    }
}
