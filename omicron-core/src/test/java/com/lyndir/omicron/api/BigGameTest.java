package com.lyndir.omicron.api;

import com.google.common.base.Throwables;
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
        otherPlayer = builder.addPlayer( null, Player.randomName(), Color.random(), Color.random() );
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
        for (final int x : coordinates)
            for (final int y : coordinates)
                gameObjectsBuilder.add( createUnit( UnitTypes.SCOUT, staticGame, staticPlayer, x, y ) );
        for (final int x : coordinates)
            for (final int y : coordinates)
                createUnit( UnitTypes.SCOUT, staticGame, otherPlayer, x + 5, y + 5 );

        long startNanos = System.nanoTime();
        staticGame.getController().setReady();
        ImmutableList<GameObject> gameObjects = gameObjectsBuilder.build();
        for (int i = 0; i < gameObjects.size(); i++) {
            GameObject gameObject = gameObjects.get( i );
            logger.dbg( "Moving gameObject %d / %d (%d%%)", i, gameObjects.size(), i * 100 / gameObjects.size() );
            gameObject.onModule( PublicModuleType.MOBILITY, 0, module -> {
                try {
                    module.getController().movement( gameObject.getLocation().get().neighbour( Side.E ).get() ).execute();
                    return Void.TYPE;
                }
                catch (IModule.ImpossibleException | IModule.InvalidatedException e) {
                    throw Throwables.propagate( e );
                }
            } );
        }
        logger.inf( "Movement took %dns", System.nanoTime() - startNanos );
    }
}
