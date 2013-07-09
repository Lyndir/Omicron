package com.lyndir.omnicron.cli;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterators;
import com.lyndir.omnicron.api.model.*;
import java.util.Iterator;


/**
 * <i>10 16, 2012</i>
 *
 * @author lhunath
 */
@CommandGroup(parent = AddCommand.class, name = "game", abbr = "g", desc = "Add things to an Omnicron game that is being built.")
public class AddGameCommand extends Command {

    @SubCommand(abbr = "p", desc = "The players that will compete in this game.")
    public void player(final OmnicronCLI omnicron, final Iterator<String> tokens) {

        Game.Builder gameBuilder = omnicron.getBuilders().getGameBuilder();
        if (gameBuilder == null) {
            err( "No game build to add game properties to.  Begin with the 'build' command." );
            return;
        }

        String value = Iterators.getOnlyElement( tokens, null );
        if (value == null) {
            err( "Missing definition of player to add.  Syntax: name,primary color,secondary color" );
            return;
        }

        Iterator<String> playerValueIt = Splitter.on( ',' ).limit( 3 ).split( value ).iterator();
        String playerName = playerValueIt.next();
        String playerPrimaryColor = playerValueIt.next();
        String playerSecondaryColor = Iterators.getOnlyElement( playerValueIt );

        Player player = new Player( gameBuilder.nextPlayerID(), playerName, Color.of( playerPrimaryColor ),
                                    Color.of( playerSecondaryColor ) );
        gameBuilder.getPlayers().add( player );
        inf( "Added player to game: %s", player );
    }
}
