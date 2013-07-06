package com.lyndir.omnicron.cli;

import java.util.Iterator;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
@CommandGroup(name = "set")
public class SetCommand extends Command {

    @SubCommand(description = "Set properties of an Omnicron game that is being built.")
    public void game(final OmnicronCLI omnicron, final Iterator<String> tokens) {

        new SetGameCommand().evaluate( omnicron, tokens );
    }
}
