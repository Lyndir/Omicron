package com.lyndir.omicron.cli;

import com.lyndir.omicron.api.model.Game;
import java.util.Iterator;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
@CommandGroup(name = "build", abbr = "b", desc = "Build a new game object.")
public class BuildCommand extends Command {

    @SubCommand(abbr = "g", desc = "Build a new game of Omicron")
    public void game(final OmicronCLI omicron, final Iterator<String> tokens) {

        omicron.getBuilders().setGameBuilder( Game.builder() );
        inf( "Building a new game of Omicron.  Configure properties with 'set'/'add'/'rm' commands, use 'create' when done." );
    }
}
