package com.lyndir.omicron.api.model;

import static org.testng.AssertJUnit.*;

import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;


public class MobilityModuleTest extends AbstractTest {

    @Test
    public void testCostForMovingInLevel()
            throws Exception {

        MobilityModule module = MobilityModule.createWithStandardResourceCost()
                                              .movementSpeed( 0 )
                                              .movementCost( ImmutableMap.of( LevelType.GROUND, 1d ) )
                                              .levelingCost( ImmutableMap.<LevelType, Double>of() );
        createUnit( testUnitType( "Ground Mover", module ) );

        assertEquals( module.costForMovingInLevel( LevelType.GROUND ), 1d );
        assertEquals( module.costForMovingInLevel( LevelType.SKY ), Double.MAX_VALUE );
        assertEquals( module.costForMovingInLevel( LevelType.SPACE ), Double.MAX_VALUE );
        module.getGameObject().getController().die();

        ImmutableMap.Builder<LevelType, Double> builder = ImmutableMap.builder();
        for (final LevelType levelType : LevelType.values())
            builder.put( levelType, (double) levelType.ordinal() );

        module = MobilityModule.createWithStandardResourceCost()
                               .movementSpeed( 0 )
                               .movementCost( builder.build() )
                               .levelingCost( ImmutableMap.<LevelType, Double>of() );
        createUnit( testUnitType( "Everywhere Mover", module ) );

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
        staticGame.getController().setReady();
        assertEquals( leveler.onModule( ModuleType.MOBILITY, 0 ).getRemainingSpeed(), 7d );

        MobilityModule.Leveling leveling = leveler.onModule( ModuleType.MOBILITY, 0 ).leveling( LevelType.GROUND );
        assertTrue( leveling.isPossible() );
        assertEquals( leveling.getTarget().getLevel().getType(), LevelType.GROUND );
        assertEquals( leveling.getCost(), 0d );
        leveling.execute();
        assertEquals( leveler.getLocation().getLevel().getType(), LevelType.GROUND );
        assertEquals( leveler.getLocation().getPosition(), new Coordinate( 0, 0, staticGame.getLevelSize() ) );
        assertEquals( leveler.onModule( ModuleType.MOBILITY, 0 ).getRemainingSpeed(), 7d );

        leveling = leveler.onModule( ModuleType.MOBILITY, 0 ).leveling( LevelType.SPACE );
        assertTrue( leveling.isPossible() );
        assertEquals( leveling.getTarget().getLevel().getType(), LevelType.SPACE );
        assertEquals( leveling.getCost(), 5d );
        leveling.execute();
        assertEquals( leveler.getLocation().getLevel().getType(), LevelType.SPACE );
        assertEquals( leveler.getLocation().getPosition(), new Coordinate( 0, 0, staticGame.getLevelSize() ) );
        assertEquals( leveler.onModule( ModuleType.MOBILITY, 0 ).getRemainingSpeed(), 2d );

        leveling = leveler.onModule( ModuleType.MOBILITY, 0 ).leveling( LevelType.SKY );
        assertTrue( leveling.isPossible() );
        assertEquals( leveling.getTarget().getLevel().getType(), LevelType.SKY );
        assertEquals( leveling.getCost(), 2d );
        leveling.execute();
        assertEquals( leveler.getLocation().getLevel().getType(), LevelType.SKY );
        assertEquals( leveler.getLocation().getPosition(), new Coordinate( 0, 0, staticGame.getLevelSize() ) );
        assertEquals( leveler.onModule( ModuleType.MOBILITY, 0 ).getRemainingSpeed(), 0d );

        leveling = leveler.onModule( ModuleType.MOBILITY, 0 ).leveling( LevelType.GROUND );
        assertFalse( leveling.isPossible() );
        assertEquals( leveling.getCost(), 1d );
        try {
            leveling.execute();
            assertFalse( true );
        } catch (Module.ImpossibleException ignored) {
        }
        assertEquals( leveler.getLocation().getLevel().getType(), LevelType.SKY );
        assertEquals( leveler.getLocation().getPosition(), new Coordinate( 0, 0, staticGame.getLevelSize() ) );
        assertEquals( leveler.onModule( ModuleType.MOBILITY, 0 ).getRemainingSpeed(), 0d );
    }

    @Test
    public void testMovement()
            throws Exception {

        GameObject mover = createUnit( testUnitType( "Mover", BaseModule.createWithStandardResourceCost()
                                                                        .maxHealth( 1 )
                                                                        .armor( 1 )
                                                                        .viewRange( 10 )
                                                                        .supportedLayers( LevelType.values() ),
                                                     MobilityModule.createWithStandardResourceCost()
                                                                   .movementSpeed( 17 )
                                                                   .movementCost(
                                                                           ImmutableMap.of( LevelType.GROUND, 1d, LevelType.SPACE, 2d ) )
                                                                   .levelingCost( ImmutableMap.of( LevelType.GROUND, 1d, LevelType.SKY, 2d,
                                                                                                   LevelType.SPACE, 3d ) ) ) );
        staticGame.getController().setReady();

        assertEquals( mover.onModule( ModuleType.MOBILITY, 0 ).getRemainingSpeed(), 17d );
        MobilityModule.Movement movement = mover.onModule( ModuleType.MOBILITY, 0 )
                                                .movement( mover.getLocation().neighbour( Coordinate.Side.E ) );
        assertTrue( movement.isPossible() );
        assertEquals( movement.getCost(), 1d );
        movement.execute();
        assertEquals( mover.getLocation().getLevel().getType(), LevelType.GROUND );
        assertEquals( mover.getLocation().getPosition(), new Coordinate( 1, 0, staticGame.getLevelSize() ) );

        movement = mover.onModule( ModuleType.MOBILITY, 0 ).movement( staticGame.getLevel( LevelType.SPACE ).getTile( 1, 5 ).get() );
        assertTrue( movement.isPossible() );
        assertEquals( movement.getCost(), 15d );
        movement.execute();
        assertEquals( mover.getLocation().getLevel().getType(), LevelType.SPACE );
        assertEquals( mover.getLocation().getPosition(), new Coordinate( 1, 5, staticGame.getLevelSize() ) );
        assertEquals( mover.onModule( ModuleType.MOBILITY, 0 ).getRemainingSpeed(), 1d );

        movement = mover.onModule( ModuleType.MOBILITY, 0 ).movement( staticGame.getLevel( LevelType.GROUND ).getTile( 0, 5 ).get() );
        assertFalse( movement.isPossible() );
        try {
            movement.execute();
            assertFalse( true );
        } catch (Module.ImpossibleException ignored) {
        }
        assertEquals( mover.getLocation().getLevel().getType(), LevelType.SPACE );
        assertEquals( mover.getLocation().getPosition(), new Coordinate( 1, 5, staticGame.getLevelSize() ) );
        assertEquals( mover.onModule( ModuleType.MOBILITY, 0 ).getRemainingSpeed(), 1d );
    }
}
