package com.lyndir.omicron.api.model;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.*;
import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.system.util.StringUtils;
import com.lyndir.omicron.api.model.*;
import java.util.*;
import javax.annotation.Nonnull;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


/**
 * @author lhunath, 2013-07-16
 */
public abstract class AbstractTest {

    protected final Logger logger = Logger.get( getClass() );

    private static final List<LevelType>           levelIndexes    = ImmutableList.of( LevelType.GROUND, LevelType.SKY, LevelType.SPACE );

    protected Game   staticGame;
    protected Player staticPlayer;

    protected void init() {
        Game.Builder builder = Game.builder();
        staticPlayer = new Player( builder.nextPlayerID(), new PlayerKey(), "testPlayer", Color.Template.randomColor(),
                                   Color.Template.randomColor() ) {
            private final PlayerController playerController = new PlayerController( this ) {
                @Override
                protected void onNewTurn() {
                    super.onNewTurn();

                    Size size = getGameController().getGame().getLevelSize();
                    Table<Integer, Integer, String> grid = HashBasedTable.create( size.getHeight(), size.getWidth() );
                    for (int u = 0; u < size.getWidth(); ++u)
                        for (int v = 0; v < size.getHeight(); ++v)
                            grid.put( v, u, "   " );

                    // Iterate observable tiles and populate the grid.
                    for (final LevelType levelType : LevelType.values()) {
                        Level level = staticGame.getLevel( levelType );
                        for (final Tile tile : level.getTiles().values()) {
                            Optional<GameObject> contents = tile.getContents();
                            if (!contents.isPresent())
                                continue;

                            char contentsChar = contents.get().getType().getTypeName().charAt( 0 );
                            int levelIndex = levelIndexes.indexOf( tile.getLevel().getType() );
                            int v = tile.getPosition().getV();
                            int u = (tile.getPosition().getU() + v / 2) % size.getWidth();
                            String tileString = grid.get( v, u );
                            StringBuilder newTileString = new StringBuilder( tileString.replace( ' ', '.' ) );
                            newTileString.setCharAt( levelIndex, contentsChar );
                            grid.put( v, u, newTileString.toString() );
                        }
                    }

                    logger.inf( "▄▄▄" + StringUtils.repeat( "▄", size.getWidth() * 4 ) );
                    for (int v = 0; v < size.getHeight(); ++v) {
                        Map<Integer, String> row = new TreeMap<>( Ordering.natural() );
                        row.putAll( grid.row( v ) );
                        logger.inf( "%s▌%s▐%s", v % 2 == 0? "": "██", Joiner.on( ' ' ).join( row.values() ), v % 2 == 0? "██": "" );
                    }
                    logger.inf( "▀▀▀" + StringUtils.repeat( "▀", size.getWidth() * 4 ) );
                }
            };

            @Nonnull
            @Override
            public PlayerController getController() {
                return playerController;
            }
        };
        builder.setLevelSize( new Size( 10, 10 ) );
        builder.setResourceConfig( Game.GameResourceConfigs.NONE );
        builder.setUnitConfig( Game.GameUnitConfigs.NONE );
        builder.getPlayers().add( staticPlayer );
        staticGame = builder.build();
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

    protected GameObject createUnit(final UnitType unitType, final int u, final int v) {
        return createUnit( unitType, staticGame, staticPlayer, u, v );
    }

    protected GameObject createUnit(final UnitType unitType, final Game game, final Player player, final int u, final int v) {
        return new GameObject( unitType, game, player, game.getLevel( LevelType.GROUND ).getTile( u, v ).get() );
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
