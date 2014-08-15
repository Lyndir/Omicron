package com.lyndir.omicron.cli.command;

import com.lyndir.omicron.api.Game;
import com.lyndir.omicron.cli.OmicronCLI;
import java.util.Iterator;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
@CommandGroup(name = "build", abbr = "b", desc = "Build a new game object.")
public class BuildCommand extends Command {

    public BuildCommand(final OmicronCLI omicron) {
        super( omicron );
    }

    @SubCommand(abbr = "g", desc = "Build a new game of Omicron")
    public void game(final Iterator<String> tokens) {

        getOmicron().getBuilders().setGameBuilder( Game.builder() );
        inf( "Building a new game of Omicron.  Configure properties with 'set'/'add'/'rm' commands, use 'create' when done." );
    }
}
