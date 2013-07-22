package com.lyndir.omicron.cli.command;

import com.google.common.collect.Iterators;
import com.lyndir.omicron.api.model.Game;
import com.lyndir.omicron.api.model.Player;
import com.lyndir.omicron.cli.OmicronCLI;
import java.util.Iterator;


/**
 * <i>10 16, 2012</i>
 *
 * @author lhunath
 */
@CommandGroup(parent = RemoveCommand.class, name = "game", abbr = "g", desc = "Remove things from an Omicron game that is being built.")
public class RemoveGameCommand extends Command {

    private Game.Builder gameBuilder;

    public RemoveGameCommand(final OmicronCLI omicron, final Game.Builder gameBuilder) {
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

    @SubCommand(abbr = "p", desc = "The players that will compete in this game.")
    public void player(final Iterator<String> tokens) {

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
