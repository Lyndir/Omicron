package com.lyndir.omicron.cli;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterators;
import com.lyndir.omicron.api.model.*;
import java.util.Iterator;


/**
 * <i>10 15, 2012</i>
 *
 * @author lhunath
 */
@CommandGroup( parent = SetCommand.class, name = "game", abbr = "g", desc = "Set properties of an Omicron game that is being built.")
public class SetGameCommand extends Command {

    private Game.Builder gameBuilder;

    @Override
    public void evaluate(final OmicronCLI omicron, final Iterator<String> tokens) {

        gameBuilder = omicron.getBuilders().getGameBuilder();
        if (gameBuilder == null) {
            err( "No game build to set game properties on.  Begin with the 'build' command." );
            return;
        }

        super.evaluate( omicron, tokens );
    }

    @SubCommand( abbr = "w", desc = "The tile dimension of each level in the game.")
    public void worldSize(final OmicronCLI omicron, final Iterator<String> tokens) {

        String gameSettingValue = Iterators.getOnlyElement( tokens, null );
        if (gameSettingValue == null) {
            inf( "worldSize (width['x'height]), currently: %s", gameBuilder.getWorldSize() );
            return;
        }

        Iterator<String> worldSizeValueIt = Splitter.on( 'x' ).limit( 2 ).split( gameSettingValue ).iterator();
        String worldSizeWidth = worldSizeValueIt.next();
        String worldSizeHeight = Iterators.getOnlyElement( worldSizeValueIt, worldSizeWidth );
        gameBuilder.setWorldSize( new Size( Integer.parseInt( worldSizeWidth ), Integer.parseInt( worldSizeHeight ) ) );
    }

    @SubCommand( abbr = "p", desc = "The players that will compete in this game.")
    public void players(final OmicronCLI omicron, final Iterator<String> tokens) {

        String gameSettingValue = Iterators.getOnlyElement( tokens, null );
        if (gameSettingValue != null) {
            err( "players cannot be set this way.  Use 'add' and 'rm' instead." );
            return;
        }

        inf( "players currently:" );
        for (final Player player : gameBuilder.getPlayers())
            inf( "    %s", player );
    }
}
