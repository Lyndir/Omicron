package com.lyndir.omnicron.cli;

import com.lyndir.omnicron.api.model.Game;
import java.util.Iterator;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
@CommandGroup(name = "create", abbr = "c", desc = "Create a previously built game object.")
public class CreateCommand extends Command {

    @SubCommand(abbr = "g", desc = "Create a new game of Omnicron")
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

        Game game = gameBuilder.build();
        omnicron.setGameController( game.getController() );
        omnicron.setLocalPlayer( gameBuilder.getPlayers().iterator().next() );
        omnicron.getBuilders().setGameBuilder( null );
        inf( "Created game: %s", game );
    }
}
