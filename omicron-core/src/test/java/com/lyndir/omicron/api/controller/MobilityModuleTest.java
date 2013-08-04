package com.lyndir.omicron.api.controller;

import static com.lyndir.omicron.api.util.AbstractTest.*;
import static org.testng.AssertJUnit.*;

import com.google.common.collect.ImmutableMap;
import com.lyndir.omicron.api.model.LevelType;
import com.lyndir.omicron.api.util.AbstractTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


public class MobilityModuleTest extends AbstractTest {

    @Test
    public void testCostForMovingInLevel()
            throws Exception {

        MobilityModule module = new MobilityModule( 0, ImmutableMap.of( LevelType.GROUND, 1d ), ImmutableMap.<LevelType, Double>of() );
        createUnit( testUnitType( "Mover", module ) );

        assertEquals( module.costForMovingInLevel( LevelType.GROUND ), 1d );
        assertEquals( module.costForMovingInLevel( LevelType.SKY ), Double.MAX_VALUE );
        assertEquals( module.costForMovingInLevel( LevelType.SPACE ), Double.MAX_VALUE );

        ImmutableMap.Builder<LevelType, Double> builder = ImmutableMap.builder();
        for (final LevelType levelType : LevelType.values())
            builder.put( levelType, (double) levelType.ordinal() );
        module = new MobilityModule( 0, builder.build(), ImmutableMap.<LevelType, Double>of() );

        for (final LevelType levelType : LevelType.values())
            assertEquals( module.costForMovingInLevel( levelType ), (double) levelType.ordinal() );
    }

    @Test
    public void testCostForLevelingToLevel()
            throws Exception {

        MobilityModule module = new MobilityModule( 0, ImmutableMap.<LevelType, Double>of(), //
                                                    ImmutableMap.of( LevelType.GROUND, 1d, LevelType.SKY, 2d, LevelType.SPACE, 3d ) );
        createUnit( testUnitType( "Leveler", module ) );

        assertEquals( module.costForLevelingToLevel( LevelType.GROUND ), 0f );
        assertEquals( module.costForLevelingToLevel( LevelType.SKY ), 2f );
        assertEquals( module.costForLevelingToLevel( LevelType.SPACE ), 5f );
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
