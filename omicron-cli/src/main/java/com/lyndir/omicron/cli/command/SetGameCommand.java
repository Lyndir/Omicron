package com.lyndir.omicron.cli.command;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterators;
import com.lyndir.lhunath.opal.math.Size;
import com.lyndir.omicron.api.core.*;
import com.lyndir.omicron.cli.OmicronCLI;
import java.util.Iterator;


/**
 * <i>10 15, 2012</i>
 *
 * @author lhunath
 */
@CommandGroup( parent = SetCommand.class, name = "game", abbr = "g", desc = "Set properties of an Omicron game that is being built.")
public class SetGameCommand extends Command {

    private IGame.IBuilder gameBuilder;

    public SetGameCommand(final OmicronCLI omicron) {
        super( omicron );
    }

    @Override
    public void evaluate(final Iterator<String> tokens) {

        gameBuilder = getOmicron().getBuilders().getGameBuilder();
        if (gameBuilder == null) {
            err( "No game build to set game properties on.  Begin with the 'build' command." );
            return;
        }

        super.evaluate( tokens );
    }

    @SubCommand(abbr = "w", desc = "The tile dimension of each level in the game.")
    public void worldSize(final Iterator<String> tokens) {

        String gameSettingValue = Iterators.getOnlyElement( tokens, null );
        if (gameSettingValue == null) {
            inf( "worldSize (width['x'height]), currently: %s", gameBuilder.getLevelSize() );
            return;
        }

        Iterator<String> worldSizeValueIt = Splitter.on( 'x' ).limit( 2 ).split( gameSettingValue ).iterator();
        String worldSizeWidth = worldSizeValueIt.next();
        String worldSizeHeight = Iterators.getOnlyElement( worldSizeValueIt, worldSizeWidth );
        gameBuilder.setLevelSize( new Size( Integer.parseInt( worldSizeWidth ), Integer.parseInt( worldSizeHeight ) ) );
    }

    @SubCommand( abbr = "p", desc = "The players that will compete in this game.")
    public void players(final Iterator<String> tokens) {

        if (tokens.hasNext()) {
            err( "players cannot be set this way.  Use 'add' and 'rm' instead." );
            return;
        }

        inf( "players currently:" );
        for (final IPlayer player : gameBuilder.getPlayers())
            inf( "    %s", player );
    }
}
