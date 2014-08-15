package com.lyndir.omicron.cli.command;

import com.google.common.base.Joiner;
import com.google.common.collect.*;
import com.lyndir.lhunath.opal.math.Size;
import com.lyndir.omicron.api.*;
import com.lyndir.omicron.api.util.Maybe;
import com.lyndir.omicron.cli.OmicronCLI;
import java.util.*;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
@CommandGroup(name = "print", abbr = "p", desc = "Print various information on the current state of the omicron game.")
public class PrintCommand extends Command {

    private static final List<LevelType>           levelIndexes    = ImmutableList.of( LevelType.GROUND, LevelType.SKY, LevelType.SPACE );
    private static final Map<LevelType, Character> levelCharacters = ImmutableMap.of( LevelType.GROUND, '_', //
                                                                                      LevelType.SKY, '~', //
                                                                                      LevelType.SPACE, '^' );

    public PrintCommand(final OmicronCLI omicron) {
        super( omicron );
    }

    @SubCommand(abbr = "f", desc = "A view of all observable tiles.")
    public void field(final Iterator<String> tokens) {

        Optional<IGameController> gameController = getOmicron().getGameController();
        if (!gameController.isPresent()) {
            err( "No game is running.  Create one with the 'create' command." );
            return;
        }

        Optional<IPlayer> localPlayerOptional = getOmicron().getLocalPlayer();
        if (!localPlayerOptional.isPresent()) {
            err( "No local player in the game." );
            return;
        }
        IPlayer localPlayer = localPlayerOptional.get();

        // Create an empty grid.
        Size findMaxSize = null;
        for (final ILevel level : gameController.get().getGame().getLevels())
            findMaxSize = Size.max( findMaxSize, level.getSize() );
        assert findMaxSize != null;
        final Size maxSize = findMaxSize;
        Table<Integer, Integer, StringBuilder> grid = HashBasedTable.create( maxSize.getHeight(), maxSize.getWidth() );
        for (int x = 0; x < maxSize.getWidth(); ++x)
            for (int y = 0; y < maxSize.getHeight(); ++y)
                grid.put( y, x, new StringBuilder( "   " ) );

        // Iterate observable tiles and populate the grid.
        localPlayer.observableTiles().forEach( tile -> {
            Maybe<? extends IGameObject> contents = tile.getContents();
            char contentsChar;
            if (contents.presence() == Maybe.Presence.PRESENT)
                contentsChar = contents.get().getType().getTypeName().charAt( 0 );
            else
                contentsChar = levelCharacters.get( tile.getLevel().getType() );

            int levelIndex = levelIndexes.indexOf( tile.getLevel().getType() );
            int y = tile.getPosition().getY();
            int x = (tile.getPosition().getX() + y / 2) % maxSize.getWidth();
            grid.get( y, x ).setCharAt( levelIndex, contentsChar );
        } );

        for (int y = 0; y < maxSize.getHeight(); ++y) {
            Map<Integer, StringBuilder> row = new TreeMap<Integer, StringBuilder>( Ordering.natural() );
            row.putAll( grid.row( y ) );
            inf( "%s|%s|", y % 2 == 0? "": "  ", Joiner.on( ' ' ).join( row.values() ) );
        }
    }
}
