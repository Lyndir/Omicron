package com.lyndir.omicron.cli;

import com.google.common.collect.Iterators;
import com.lyndir.omicron.api.model.Game;
import com.lyndir.omicron.api.model.Player;
import java.util.Iterator;


/**
 * <i>10 16, 2012</i>
 *
 * @author lhunath
 */
@CommandGroup(parent = RemoveCommand.class, name = "game", abbr = "g", desc = "Remove things from an Omicron game that is being built.")
public class RemoveGameCommand extends Command {

    private Game.Builder gameBuilder;

    @Override
    public void evaluate(final OmicronCLI omicron, final Iterator<String> tokens) {

        gameBuilder = omicron.getBuilders().getGameBuilder();
        if (gameBuilder == null) {
            err( "No game build to add game properties to.  Begin with the 'build' command." );
            return;
        }

        super.evaluate( omicron, tokens );
    }

    @SubCommand(abbr = "p", desc = "The players that will compete in this game.")
    public void player(final OmicronCLI omicron, final Iterator<String> tokens) {

        String value = Iterators.getOnlyElement( tokens, null );
        if (value == null) {
            err( "Missing definition of player to add.  Syntax: name,primary color,secondary color" );
            return;
        }

        Iterator<Player> playerIt = gameBuilder.getPlayers().iterator();
        while (playerIt.hasNext()) {
            if (playerIt.next().getName().equals( value )) {
                playerIt.remove();
                break;
            }
        }
    }
}
