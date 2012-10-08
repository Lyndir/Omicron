package com.lyndir.omnicron.cli;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterators;
import com.lyndir.omnicron.api.Game;
import com.lyndir.omnicron.api.Size;
import java.util.Iterator;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
public class NewGameCommand implements Command {

    private static final Splitter gameSettingCommandSplitter = Splitter.on( ':' ).limit( 2 );

    @Override
    public void evaluate(final OmnicronCLI omnicron, final Iterator<String> tokens) {

        Game.Builder gameBuilder = Game.builder();

        while (tokens.hasNext()) {
            Iterator<String> gameSettingIt = gameSettingCommandSplitter.split( tokens.next() ).iterator();
            String gameSetting = gameSettingIt.next();
            String gameSettingValue = Iterators.getOnlyElement( gameSettingIt, null );

            if ("worldSize".equals( gameSetting )) {
                if (gameSettingValue == null) {
                    System.err.println("worldSize requires a size value width['x'height].  If height is omitted, a square worldSize is assumed.");
                    return;
                }

                Iterator<String> worldSizeValueIt = Splitter.on( 'x' ).limit( 2 ).split( gameSettingValue ).iterator();
                String worldSizeWidth = worldSizeValueIt.next();
                String worldSizeHeight = Iterators.getOnlyElement( worldSizeValueIt, worldSizeWidth );
                gameBuilder.setWorldSize( new Size( Integer.parseInt( worldSizeWidth ), Integer.parseInt( worldSizeHeight ) ) );
            }
            else {
                System.err.format("new game: Unknown setting: %s\n", gameSetting);
                return;
            }
        }

        omnicron.setGame( gameBuilder.build() );
    }
}
