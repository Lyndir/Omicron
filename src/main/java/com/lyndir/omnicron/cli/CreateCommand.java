package com.lyndir.omnicron.cli;

import com.lyndir.omnicron.api.Game;
import java.util.Iterator;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
@CommandGroup(name = "create")
public class CreateCommand extends Command {

    @SubCommand(description = "Create a new game of Omnicron")
    public void game(final OmnicronCLI omnicron, final Iterator<String> tokens) {

        Game.Builder gameBuilder = omnicron.getBuilders().getGameBuilder();
        if (gameBuilder == null) {
            err( "No game has been built yet.  Begin with the 'build' command." );
            return;
        }
        if (gameBuilder.getPlayers().isEmpty()) {
            err( "No local player has been added yet.  Add players with the 'add' command." );
            return;
        }

        omnicron.setGame( gameBuilder.build() );
        omnicron.setLocalPlayer( gameBuilder.getPlayers().iterator().next() );
        omnicron.getBuilders().setGameBuilder( null );
    }
}
