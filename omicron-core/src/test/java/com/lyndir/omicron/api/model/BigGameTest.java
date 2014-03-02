package com.lyndir.omicron.api.model;

import com.google.common.collect.ImmutableList;
import com.lyndir.lhunath.opal.math.Side;
import com.lyndir.lhunath.opal.math.Size;
import org.testng.annotations.Test;


/**
 * @author lhunath, 2014-03-01
 */
public class BigGameTest extends AbstractTest {

    private Player otherPlayer;

    @Override
    protected Game.Builder newGameBuilder() {
        Game.Builder builder = super.newGameBuilder();
        builder.addPlayer( otherPlayer = new Player( builder.nextPlayerID(), null, Player.randomName(), Color.random(), Color.random() ) );
        builder.setLevelSize( new Size( 1000, 1000 ) );
        return builder;
    }

    @Override
    protected void printWorldMap() {
    }

    @Test
    public void testBigGame()
            throws Exception {

        int[] coordinates = {
                0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 130, 140, 150, 160, 170, 180, 190, 200, 210, 220, 230, 240, 250, 260,
                270, 280, 290 };

        ImmutableList.Builder<GameObject> gameObjectsBuilder = ImmutableList.builder();
        for (int x = 0; x < coordinates.length; ++x) {
            for (int y = 0; y < coordinates.length; ++y) {
                gameObjectsBuilder.add( createUnit( UnitTypes.SCOUT, staticGame, staticPlayer, coordinates[x], coordinates[y] ) );
            }
        }
        for (int x = 0; x < coordinates.length; ++x) {
            for (int y = 0; y < coordinates.length; ++y) {
                createUnit( UnitTypes.SCOUT, staticGame, otherPlayer, coordinates[x] + 5, coordinates[y] + 5 );
            }
        }

        long startNanos = System.nanoTime();
        staticGame.getController().setReady();
        ImmutableList<GameObject> gameObjects = gameObjectsBuilder.build();
        for (int i = 0; i < gameObjects.size(); i++) {
            GameObject gameObject = gameObjects.get( i );
            logger.dbg( "Moving gameObject %d / %d (%d%%)", i,   gameObjects.size(), i * 100 / gameObjects.size() );
            gameObject.onModule( PublicModuleType.MOBILITY, 0 )
                      .movement( gameObject.getLocation().get().neighbour( Side.E ).get() )
                      .execute();
        }
        logger.inf( "Movement took %dns", System.nanoTime() - startNanos );
    }
}
