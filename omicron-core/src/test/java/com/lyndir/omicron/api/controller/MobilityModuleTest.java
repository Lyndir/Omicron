package com.lyndir.omicron.api.controller;

import static org.testng.AssertJUnit.*;

import com.google.common.collect.ImmutableMap;
import com.lyndir.omicron.api.model.*;
import org.testng.annotations.Test;


public class MobilityModuleTest {

    @Test
    public void testCostForMovingInLevel()
            throws Exception {

        MobilityModule mobilityModule = new MobilityModule( 0, ImmutableMap.of( LevelType.GROUND, 1f ),
                                                            ImmutableMap.<LevelType, Float>of() );
        initModule( mobilityModule );

        assertEquals( mobilityModule.costForMovingInLevel( LevelType.GROUND ), 1f );
        assertEquals( mobilityModule.costForMovingInLevel( LevelType.SKY ), Float.MAX_VALUE );
        assertEquals( mobilityModule.costForMovingInLevel( LevelType.SPACE ), Float.MAX_VALUE );

        ImmutableMap.Builder<LevelType, Float> builder = ImmutableMap.builder();
        for (final LevelType levelType : LevelType.values())
            builder.put( levelType, (float) levelType.ordinal() );
        mobilityModule = new MobilityModule( 0, builder.build(), ImmutableMap.<LevelType, Float>of() );

        for (final LevelType levelType : LevelType.values())
            assertEquals( mobilityModule.costForMovingInLevel( levelType ), (float) levelType.ordinal() );
    }

    @Test
    public void testCostForLevelingToLevel()
            throws Exception {

        MobilityModule mobilityModule = new MobilityModule( 0, ImmutableMap.<LevelType, Float>of(),
                                                            ImmutableMap.of( LevelType.GROUND, 1f, //
                                                                             LevelType.SKY, 2f, //
                                                                             LevelType.SPACE, 3f ) );
        initModule( mobilityModule );

        assertEquals( mobilityModule.costForLevelingToLevel( LevelType.GROUND ), 0f );
        assertEquals( mobilityModule.costForLevelingToLevel( LevelType.SKY ), 2f );
        assertEquals( mobilityModule.costForLevelingToLevel( LevelType.SPACE ), 5f );
    }

    private static void initModule(final MobilityModule mobilityModule) {

        Game.Builder builder = Game.builder();
        Player player = new Player( builder.nextPlayerID(), new PlayerKey(), "testPlayer", Color.Template.randomColor(),
                                    Color.Template.randomColor() );
        builder.getPlayers().add( player );
        Game game = builder.build();

        mobilityModule.setGameObject(
                new Scout( new Tile( new Coordinate( 0, 0, new Size( 10, 10 ) ), new Level( new Size( 10, 10 ), LevelType.GROUND, game ) ),
                           player ) );
    }

    @Test
    public void testMove()
            throws Exception {

    }

    @Test
    public void testLevel()
            throws Exception {

    }
}
