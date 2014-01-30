package com.lyndir.omicron.cli.command;

import com.google.common.collect.Iterators;
import com.lyndir.omicron.api.model.*;
import com.lyndir.omicron.cli.OmicronCLI;
import java.util.Iterator;


/**
 * <i>10 16, 2012</i>
 *
 * @author lhunath
 */
@CommandGroup(parent = RemoveCommand.class, name = "game", abbr = "g", desc = "Remove things from an Omicron game that is being built.")
public class RemoveGameCommand extends Command {

    private IGame.IBuilder gameBuilder;

    public RemoveGameCommand(final OmicronCLI omicron) {
        super( omicron );
    }

    @Override
    public void evaluate(final Iterator<String> tokens) {

        gameBuilder = getOmicron().getBuilders().getGameBuilder();
        if (gameBuilder == null) {
            err( "No game build to add game properties to.  Begin with the 'build' command." );
            return;
        }

        super.evaluate( tokens );
    }

    @SubCommand(abbr = "p", desc = "Remove a player from the game.")
    public void player(final Iterator<String> tokens) {

        String value = Iterators.getOnlyElement( tokens, null );
        if (value == null) {
            err( "Missing name of player to remove." );
            return;
        }

        Iterator<IPlayer> playerIt = gameBuilder.getPlayers().iterator();
        while (playerIt.hasNext()) {
            if (playerIt.next().getName().equals( value )) {
                playerIt.remove();
                break;
            }
        }
    }
}
