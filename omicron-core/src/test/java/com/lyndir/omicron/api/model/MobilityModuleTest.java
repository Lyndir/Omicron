package com.lyndir.omicron.api.model;

import static org.testng.AssertJUnit.*;

import com.google.common.collect.ImmutableMap;
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

        assertEquals( module.costForLevelingToLevel( LevelType.GROUND ), 0d );
        assertEquals( module.costForLevelingToLevel( LevelType.SKY ), 2d );
        assertEquals( module.costForLevelingToLevel( LevelType.SPACE ), 5d );
    }

    @Test
    public void testLevel()
            throws Exception {

        GameObject leveler = createUnit( testUnitType( "Leveler", MobilityModule.createWithStandardResourceCost()
                                                                                .movementSpeed( 7 )
                                                                                .movementCost( ImmutableMap.<LevelType, Double>of() )
                                                                                .levelingCost( ImmutableMap.of( LevelType.GROUND, 1d,
                                                                                                                LevelType.SKY, 2d,
                                                                                                                LevelType.SPACE, 3d ) ) ) );
        staticGame.getController().start();
        assertEquals( leveler.onModule( ModuleType.MOBILITY, 0 ).getRemainingSpeed(), 7d );

        MobilityModule.Leveling leveling = leveler.onModule( ModuleType.MOBILITY, 0 ).leveling( staticPlayer, LevelType.GROUND );
        assertTrue( leveling.isPossible() );
        assertEquals( leveling.getTarget().getLevel().getType(), LevelType.GROUND );
        assertEquals( leveling.getCost(), 0d );
        leveling.execute();
        assertEquals( leveler.getLocation().getLevel().getType(), LevelType.GROUND );
        assertEquals( leveler.getLocation().getPosition(), new Coordinate( 0, 0, staticGame.getLevelSize() ) );
        assertEquals( leveler.onModule( ModuleType.MOBILITY, 0 ).getRemainingSpeed(), 7d );

        leveling = leveler.onModule( ModuleType.MOBILITY, 0 ).leveling( staticPlayer, LevelType.SPACE );
        assertTrue( leveling.isPossible() );
        assertEquals( leveling.getTarget().getLevel().getType(), LevelType.SPACE );
        assertEquals( leveling.getCost(), 5d );
        leveling.execute();
        assertEquals( leveler.getLocation().getLevel().getType(), LevelType.SPACE );
        assertEquals( leveler.getLocation().getPosition(), new Coordinate( 0, 0, staticGame.getLevelSize() ) );
        assertEquals( leveler.onModule( ModuleType.MOBILITY, 0 ).getRemainingSpeed(), 2d );

        leveling = leveler.onModule( ModuleType.MOBILITY, 0 ).leveling( staticPlayer, LevelType.SKY );
        assertTrue( leveling.isPossible() );
        assertEquals( leveling.getTarget().getLevel().getType(), LevelType.SKY );
        assertEquals( leveling.getCost(), 2d );
        leveling.execute();
        assertEquals( leveler.getLocation().getLevel().getType(), LevelType.SKY );
        assertEquals( leveler.getLocation().getPosition(), new Coordinate( 0, 0, staticGame.getLevelSize() ) );
        assertEquals( leveler.onModule( ModuleType.MOBILITY, 0 ).getRemainingSpeed(), 0d );

        leveling = leveler.onModule( ModuleType.MOBILITY, 0 ).leveling( staticPlayer, LevelType.GROUND );
        assertFalse( leveling.isPossible() );
        assertEquals( leveling.getTarget().getLevel().getType(), LevelType.GROUND );
        assertEquals( leveling.getCost(), 1d );
        leveling.execute();
        assertEquals( leveler.getLocation().getLevel().getType(), LevelType.SKY );
        assertEquals( leveler.getLocation().getPosition(), new Coordinate( 0, 0, staticGame.getLevelSize() ) );
        assertEquals( leveler.onModule( ModuleType.MOBILITY, 0 ).getRemainingSpeed(), 0d );
    }

    @Test
    public void testMovement()
            throws Exception {

        GameObject mover = createUnit( testUnitType( "Mover", MobilityModule.createWithStandardResourceCost()
                                                                            .movementSpeed( 17 )
                                                                            .movementCost(
                                                                                    ImmutableMap.of( LevelType.GROUND, 1d, LevelType.SPACE,
                                                                                                     2d ) )
                                                                            .levelingCost(
                                                                                    ImmutableMap.of( LevelType.GROUND, 1d, LevelType.SKY,
                                                                                                     2d, LevelType.SPACE, 3d ) ) ) );
        staticGame.getController().start();

        assertEquals( mover.onModule( ModuleType.MOBILITY, 0 ).getRemainingSpeed(), 17d );
        MobilityModule.Movement movement = mover.onModule( ModuleType.MOBILITY, 0 )
                                                .movement( staticPlayer, mover.getLocation().neighbour( Coordinate.Side.E ) );
        assertTrue( movement.isPossible() );
        assertEquals( movement.getCost(), 1d );
        movement.execute();
        assertEquals( mover.getLocation().getLevel().getType(), LevelType.GROUND );
        assertEquals( mover.getLocation().getPosition(), new Coordinate( 1, 0, staticGame.getLevelSize() ) );

        movement = mover.onModule( ModuleType.MOBILITY, 0 )
                        .movement( staticPlayer, staticGame.getLevel( LevelType.SPACE ).getTile( 1, 5 ).get() );
        assertTrue( movement.isPossible() );
        assertEquals( movement.getCost(), 15d );
        movement.execute();
        assertEquals( mover.getLocation().getLevel().getType(), LevelType.SPACE );
        assertEquals( mover.getLocation().getPosition(), new Coordinate( 1, 5, staticGame.getLevelSize() ) );
        assertEquals( mover.onModule( ModuleType.MOBILITY, 0 ).getRemainingSpeed(), 1d );

        movement = mover.onModule( ModuleType.MOBILITY, 0 )
                        .movement( staticPlayer, staticGame.getLevel( LevelType.GROUND ).getTile( 0, 5 ).get() );
        assertFalse( movement.isPossible() );
        movement.execute();
        assertEquals( mover.getLocation().getLevel().getType(), LevelType.SPACE );
        assertEquals( mover.getLocation().getPosition(), new Coordinate( 1, 5, staticGame.getLevelSize() ) );
        assertEquals( mover.onModule( ModuleType.MOBILITY, 0 ).getRemainingSpeed(), 1d );
    }
}
