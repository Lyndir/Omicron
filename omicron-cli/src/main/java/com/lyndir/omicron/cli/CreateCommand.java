package com.lyndir.omicron.cli;

import com.lyndir.omicron.api.model.Game;
import java.util.Iterator;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
@CommandGroup(name = "create", abbr = "c", desc = "Create a previously built game object.")
public class CreateCommand extends Command {

    @SubCommand(abbr = "g", desc = "Create a new game of Omicron")
    public void game(final OmicronCLI omicron, final Iterator<String> tokens) {

        Game.Builder gameBuilder = omicron.getBuilders().getGameBuilder();
        if (gameBuilder == null) {
            err( "No game has been built yet.  Begin with the 'build' command." );
            return;
        }
        if (gameBuilder.getPlayers().isEmpty()) {
            err( "No local player has been added yet.  Add players with the 'add' command." );
            return;
        }

        Game game = gameBuilder.build();
        omicron.setGameController( game.getController() );
        omicron.setLocalPlayer( gameBuilder.getPlayers().iterator().next() );
        omicron.getBuilders().setGameBuilder( null );
        inf( "Created game: %s", game );
    }
}
