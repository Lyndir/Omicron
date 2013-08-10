package com.lyndir.omicron.cli.command;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.*;
import com.lyndir.omicron.api.model.GameController;
import com.lyndir.omicron.api.model.*;
import com.lyndir.omicron.cli.OmicronCLI;
import java.util.*;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
@CommandGroup(name = "print", abbr = "p", desc = "Print various information on the current state of the omicron game.")
public class PrintCommand extends Command {

    private static final List<Class<? extends Level>>           levelIndexes    = ImmutableList.of( GroundLevel.class, SkyLevel.class,
                                                                                                    SpaceLevel.class );
    private static final Map<Class<? extends Level>, Character> levelCharacters = ImmutableMap.of( GroundLevel.class, '_', //
                                                                                                   SkyLevel.class, '~', //
                                                                                                   SpaceLevel.class, '^' );

    public PrintCommand(final OmicronCLI omicron) {
        super( omicron );
    }

    @SubCommand(abbr = "f", desc = "A view of all observable tiles.")
    public void field(final Iterator<String> tokens) {

        final Optional<GameController> gameController = getOmicron().getGameController();
        if (!gameController.isPresent()) {
            err( "No game is running.  Create one with the 'create' command." );
            return;
        }

        final Optional<Player> localPlayerOptional = getOmicron().getLocalPlayer();
        if (!localPlayerOptional.isPresent()) {
            err( "No local player in the game." );
            return;
        }
        final Player localPlayer = localPlayerOptional.get();

        // Create an empty grid.
        Size maxSize = null;
        for (final Level level : gameController.get().listLevels())
            maxSize = Size.max( maxSize, level.getSize() );
        assert maxSize != null;
        Table<Integer, Integer, StringBuilder> grid = HashBasedTable.create( maxSize.getHeight(), maxSize.getWidth() );
        for (int u = 0; u < maxSize.getWidth(); ++u)
            for (int v = 0; v < maxSize.getHeight(); ++v)
                grid.put( v, u, new StringBuilder( "   " ) );

        // Iterate observable tiles and populate the grid.
        for (final Tile tile : localPlayer.listObservableTiles()) {
            Optional<GameObject> contents = tile.getContents();
            char contentsChar;
            if (contents.isPresent())
                contentsChar = contents.get().getType().getTypeName().charAt( 0 );
            else
                contentsChar = levelCharacters.get( tile.getLevel().getClass() );

            int levelIndex = levelIndexes.indexOf( tile.getLevel().getClass() );
            int v = tile.getPosition().getV();
            int u = (tile.getPosition().getU() + v / 2) % maxSize.getWidth();
            grid.get( v, u ).setCharAt( levelIndex, contentsChar );
        }

        for (int v = 0; v < maxSize.getHeight(); ++v) {
            Map<Integer, StringBuilder> row = new TreeMap<>( Ordering.natural() );
            row.putAll( grid.row( v ) );
            inf( "%s|%s|", v % 2 == 0? "": "  ", Joiner.on( ' ' ).join( row.values() ) );
        }
    }
}
