package com.lyndir.omnicron.cli;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.lyndir.omnicron.api.model.*;
import java.util.*;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
@CommandGroup(name = "print", abbr = "p", desc = "Print various information on the current state of the omnicron game.")
public class PrintCommand extends Command {

    @SubCommand(abbr = "f", desc = "A view of all observable tiles.")
    public void field(final OmnicronCLI omnicron, final Iterator<String> tokens) {

        ImmutableMap.Builder<Level, ArrayList<ArrayList<Character>>> gridsBuilder = ImmutableMap.builder();
        for (final Level level : omnicron.getGameController().listLevels()) {
            ArrayList<ArrayList<Character>> us = new ArrayList<>( level.getLevelSize().getWidth() );
            for (int u = 0; u < level.getLevelSize().getWidth(); ++u) {
                ArrayList<Character> vs = new ArrayList<>( level.getLevelSize().getHeight() );
                for (int v = 0; v < level.getLevelSize().getHeight(); ++v)
                    vs.add( ' ' );
                us.add( vs );
            }
            gridsBuilder.put( level, us );
        }
        ImmutableMap<Level, ArrayList<ArrayList<Character>>> grids = gridsBuilder.build();

        for (final Tile tile : omnicron.getLocalPlayer().listObservableTiles( omnicron.getLocalPlayer() )) {
            GameObject contents = tile.getContents();
            char contentsChar = '.';
            if (contents != null)
                contentsChar = contents.getTypeName().charAt( 0 );

            grids.get( tile.getLevel() ).get( tile.getPosition().getU() ).set( tile.getPosition().getV(), contentsChar );
        }

        for (final Level level : grids.keySet()) {
            inf( "%s:", level.getName() );
            Iterable<ArrayList<Character>> us = grids.get( level );
            StringBuilder indent = new StringBuilder();
            for (final Iterable<Character> vs : us) {
                inf( "%s\\%s\\", indent.toString(), Joiner.on(' ').join( vs ) );
                indent.append( ' ' );
            }
        }
    }
}
