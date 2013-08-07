package com.lyndir.omicron.api.model;

import static org.testng.AssertJUnit.*;

import com.google.common.collect.ImmutableMap;
import com.lyndir.omicron.api.model.LevelType;
import com.lyndir.omicron.api.model.MobilityModule;
import com.lyndir.omicron.api.util.AbstractTest;
import org.testng.annotations.Test;


public class MobilityModuleTest extends AbstractTest {

    @Test
    public void testCostForMovingInLevel()
            throws Exception {

        MobilityModule module = MobilityModule.createWithStandardResourceCost()
                                              .movementSpeed( 0 )
                                              .movementCost( ImmutableMap.of( LevelType.GROUND, 1d ) )
                                              .levelingCost( ImmutableMap.<LevelType, Double>of() );
        createUnit( testUnitType( "Mover", module ) );

        assertEquals( module.costForMovingInLevel( LevelType.GROUND ), 1d );
        assertEquals( module.costForMovingInLevel( LevelType.SKY ), Double.MAX_VALUE );
        assertEquals( module.costForMovingInLevel( LevelType.SPACE ), Double.MAX_VALUE );

        ImmutableMap.Builder<LevelType, Double> builder = ImmutableMap.builder();
        for (final LevelType levelType : LevelType.values())
            builder.put( levelType, (double) levelType.ordinal() );
        module = MobilityModule.createWithStandardResourceCost()
                               .movementSpeed( 0 )
                               .movementCost( builder.build() )
                               .levelingCost( ImmutableMap.<LevelType, Double>of() );

        for (final LevelType levelType : LevelType.values())
            assertEquals( module.costForMovingInLevel( levelType ), (double) levelType.ordinal() );
    }

    @Test
    public void testCostForLevelingToLevel()
            throws Exception {

        MobilityModule module = MobilityModule.createWithStandardResourceCost()
                                              .movementSpeed( 0 )
                                              .movementCost( ImmutableMap.<LevelType, Double>of() )
                                              .levelingCost(
                                                      ImmutableMap.of( LevelType.GROUND, 1d, LevelType.SKY, 2d, LevelType.SPACE, 3d ) );
        createUnit( testUnitType( "Leveler", module ) );

        assertEquals( module.costForLevelingToLevel( LevelType.GROUND ), 0f );
        assertEquals( module.costForLevelingToLevel( LevelType.SKY ), 2f );
        assertEquals( module.costForLevelingToLevel( LevelType.SPACE ), 5f );
    }

    @Test
    public void testMovement()
            throws Exception {
        // TODO
    }

    @Test
    public void testLevel()
            throws Exception {
        // TODO
    }
}
