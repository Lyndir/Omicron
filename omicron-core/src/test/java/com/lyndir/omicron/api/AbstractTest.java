package com.lyndir.omicron.api;

import com.google.common.base.Joiner;
import com.google.common.collect.*;
import com.lyndir.lhunath.opal.math.Size;
import com.lyndir.lhunath.opal.math.Vec2;
import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.system.util.StringUtils;
import com.lyndir.omicron.api.util.Maybe;
import java.util.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


/**
 * @author lhunath, 2013-07-16
 */
public abstract class AbstractTest {

    @SuppressWarnings("NonConstantLogger")
    protected final Logger logger = Logger.get( getClass() );

    private static final List<LevelType> levelIndexes = ImmutableList.of( LevelType.GROUND, LevelType.SKY, LevelType.SPACE );

    protected Game   staticGame;
    protected Player staticPlayer;

    protected void init() {
        staticGame = newGameBuilder().build();
    }

    protected Game.Builder newGameBuilder() {
        Game.Builder builder = Game.builder();
        builder.setLevelSize( new Size( 10, 10 ) );
        builder.setResourceConfig( IGame.GameResourceConfigs.NONE );
        builder.setUnitConfig( IGame.PublicGameUnitConfig.NONE );

        Security.activatePlayer( staticPlayer = builder.addPlayer( new PlayerKey(), "testPlayer", Color.Template.randomColor(),
                                                                   Color.Template.randomColor() ) );

        builder.addGameListener( new GameListener() {
            @Override
            public void onNewTurn(final Turn currentTurn) {
                super.onNewTurn( currentTurn );
                printWorldMap();
            }
        } );

        return builder;
    }

    protected void printWorldMap() {
        Size size = staticPlayer.getController().getGameController().getGame().getLevelSize();
        Table<Integer, Integer, String> grid = HashBasedTable.create( size.getHeight(), size.getWidth() );
        for (int u = 0; u < size.getWidth(); ++u)
            for (int v = 0; v < size.getHeight(); ++v)
                grid.put( v, u, "   " );

        // Iterate observable tiles and populate the grid.
        for (final LevelType levelType : LevelType.values()) {
            ILevel level = staticGame.getLevel( levelType );
            for (final ITile tile : level.getTilesByPosition().values()) {
                Maybe<? extends IGameObject> contents = tile.getContents();
                if (!contents.isPresent())
                    continue;

                char contentsChar = contents.get().getType().getTypeName().charAt( 0 );
                int levelIndex = levelIndexes.indexOf( tile.getLevel().getType() );
                int v = tile.getPosition().getY();
                int u = (tile.getPosition().getX() + v / 2) % size.getWidth();
                String tileString = grid.get( v, u );
                StringBuilder newTileString = new StringBuilder( tileString.replace( ' ', '.' ) );
                newTileString.setCharAt( levelIndex, contentsChar );
                grid.put( v, u, newTileString.toString() );
            }
        }

        logger.inf( "▄▄▄" + StringUtils.repeat( "▄", size.getWidth() * 4 ) );
        for (int v = 0; v < size.getHeight(); ++v) {
            Map<Integer, String> row = new TreeMap<Integer, String>( Ordering.natural() );
            row.putAll( grid.row( v ) );
            logger.inf( "%s▌%s▐%s", v % 2 == 0? "": "██", Joiner.on( ' ' ).join( row.values() ), v % 2 == 0? "██": "" );
        }
        logger.inf( "▀▀▀" + StringUtils.repeat( "▀", size.getWidth() * 4 ) );
    }

    protected UnitType testUnitType(final String typeName, final Module... modules) {
        return testUnitType( typeName, 0, modules );
    }

    protected UnitType testUnitType(final String typeName, final int constructionWork, final Module... modules) {
        return new UnitType() {
            @Override
            public String getTypeName() {
                return typeName;
            }

            @Override
            public int getConstructionWork() {
                return constructionWork;
            }

            @Override
            public ImmutableList<? extends Module> createModules() {
                return ImmutableList.copyOf( modules );
            }

            @Override
            public String toString() {
                return String.format( "{%s: %s}", getTypeName(), createModules() );
            }
        };
    }

    protected GameObject createUnit(final UnitType unitType) {
        return createUnit( unitType, 0, 0 );
    }

    protected GameObject createUnit(final UnitType unitType, final int x, final int y) {
        return createUnit( unitType, staticGame, staticPlayer, x, y );
    }

    protected GameObject createUnit(final UnitType unitType, final Game game, final Player player, final int x, final int y) {
        Tile tile = Tile.cast( game.getLevel( LevelType.GROUND ).getTile( Vec2.create( x, y ) ).get() );

        return createUnit( unitType, game, player, tile );
    }

    protected GameObject createUnit(final UnitType unitType, final Game game, final Player player, final Tile tile) {
        GameObject gameObject = new GameObject( unitType, game, player, tile );
        gameObject.register();

        return gameObject;
    }

    @BeforeMethod
    public void setUp()
            throws Exception {

        init();
    }

    @Test(enabled = false)
    public void testNothing()
            throws Exception {
    }
}
