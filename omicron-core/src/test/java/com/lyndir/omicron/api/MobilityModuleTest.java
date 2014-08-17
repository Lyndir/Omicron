package com.lyndir.omicron.api;

import static org.testng.AssertJUnit.*;

import com.google.common.collect.ImmutableMap;
import com.lyndir.lhunath.opal.math.*;
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

        assertEquals( 1d, module.costForMovingInLevel( LevelType.GROUND ) );
        assertEquals( Double.MAX_VALUE, module.costForMovingInLevel( LevelType.SKY ) );
        assertEquals( Double.MAX_VALUE, module.costForMovingInLevel( LevelType.SPACE ) );
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
            assertEquals( (double) levelType.ordinal(), module.costForMovingInLevel( levelType ) );
    }

    @Test
    public void testCostForLevelingToLevel()
            throws Exception {

        MobilityModule module = MobilityModule.createWithStandardResourceCost()
                                              .movementSpeed( 0 )
                                              .movementCost( ImmutableMap.<LevelType, Double>of() )
                                              .levelingCost(
                                                      ImmutableMap.of( LevelType.GROUND, 1d, LevelType.SKY, 2d ) );
        createUnit( testUnitType( "Leveler", module ) );

        assertEquals( 0d, module.costForLevelingToLevel( LevelType.GROUND ) );
        assertEquals( 1d, module.costForLevelingToLevel( LevelType.SKY ) );
        assertEquals( 3d, module.costForLevelingToLevel( LevelType.SPACE ) );
    }

    @Test
    public void testLevelling()
            throws Exception {

        GameObject leveler = createUnit( testUnitType( "Leveler", MobilityModule.createWithStandardResourceCost()
                                                                                .movementSpeed( 5 )
                                                                                .movementCost( ImmutableMap.<LevelType, Double>of() )
                                                                                .levelingCost( ImmutableMap.of( LevelType.GROUND, 1d,
                                                                                                                LevelType.SKY, 2d ) ) ) );
        staticGame.getController().setReady();
        assertEquals( 5d, leveler.onModule( ModuleType.MOBILITY, 0, MobilityModule::getRemainingSpeed ) );

        MobilityModule.Leveling leveling = leveler.onModule( ModuleType.MOBILITY, 0, module -> module.leveling( LevelType.GROUND ) );
        assertTrue( leveling.isPossible() );
        assertEquals( LevelType.GROUND, leveling.getTarget().getLevel().getType() );
        assertEquals( 0d, leveling.getCost() );
        leveling.execute();
        assertEquals( LevelType.GROUND, leveler.getLocation().get().getLevel().getType() );
        assertEquals( Vec2.create( 0, 0 ), leveler.getLocation().get().getPosition() );
        assertEquals( 5d, leveler.onModule( ModuleType.MOBILITY, 0, MobilityModule::getRemainingSpeed ) );

        leveling = leveler.onModule( ModuleType.MOBILITY, 0, module -> module.leveling( LevelType.SPACE ) );
        assertTrue( leveling.isPossible() );
        assertEquals( LevelType.SPACE, leveling.getTarget().getLevel().getType() );
        assertEquals( 3d, leveling.getCost() );
        leveling.execute();
        assertEquals( LevelType.SPACE, leveler.getLocation().get().getLevel().getType() );
        assertEquals( Vec2.create( 0, 0 ), leveler.getLocation().get().getPosition() );
        assertEquals( 2d, leveler.onModule( ModuleType.MOBILITY, 0, MobilityModule::getRemainingSpeed ) );

        leveling = leveler.onModule( ModuleType.MOBILITY, 0, module -> module.leveling( LevelType.SKY ) );
        assertTrue( leveling.isPossible() );
        assertEquals( LevelType.SKY, leveling.getTarget().getLevel().getType() );
        assertEquals( 2d, leveling.getCost() );
        leveling.execute();
        assertEquals( LevelType.SKY, leveler.getLocation().get().getLevel().getType() );
        assertEquals( Vec2.create( 0, 0 ), leveler.getLocation().get().getPosition() );
        assertEquals( 0d, leveler.onModule( ModuleType.MOBILITY, 0, MobilityModule::getRemainingSpeed ) );

        leveling = leveler.onModule( ModuleType.MOBILITY, 0, module -> module.leveling( LevelType.GROUND ) );
        assertFalse( leveling.isPossible() );
        assertEquals( 1d, leveling.getCost() );
        try {
            leveling.execute();
            assertFalse( true );
        }
        catch (final Module.ImpossibleException ignored) {
        }
        assertEquals( LevelType.SKY, leveler.getLocation().get().getLevel().getType() );
        assertEquals( Vec2.create( 0, 0 ), leveler.getLocation().get().getPosition() );
        assertEquals( 0d, leveler.onModule( ModuleType.MOBILITY, 0, MobilityModule::getRemainingSpeed ) );
    }

    @Test
    public void testBasicMovement()
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
                                                                   .levelingCost(
                                                                           ImmutableMap.of( LevelType.GROUND, 1d, LevelType.SKY, 2d ) ) ) );
        staticGame.getController().setReady();

        // Walk east on the ground.
        assertEquals( 17d, mover.onModule( ModuleType.MOBILITY, 0, MobilityModule::getRemainingSpeed ) );
        MobilityModule.Movement movement = mover.onModule( ModuleType.MOBILITY, 0, module -> module.movement(
                mover.getLocation().get().neighbour( Side.E ).get() ) );
        assertTrue( movement.isPossible() );
        assertEquals( 1d, movement.getCost() );
        movement.execute();
        assertEquals( LevelType.GROUND, mover.getLocation().get().getLevel().getType() );
        assertEquals( Vec2.create( 1, 0 ), mover.getLocation().get().getPosition() );

        // Level to space.
        movement = mover.onModule( ModuleType.MOBILITY, 0, module -> module.movement(
                staticGame.getLevel( LevelType.SPACE ).getTile( Vec2.create( 1, 0 ) ).get() ) );
        assertTrue( movement.isPossible() );
        assertEquals( 3d, movement.getCost() );
        movement.execute();
        assertEquals( LevelType.SPACE, mover.getLocation().get().getLevel().getType() );
        assertEquals( Vec2.create( 1, 0 ), mover.getLocation().get().getPosition() );
        assertEquals( 13d, mover.onModule( ModuleType.MOBILITY, 0, MobilityModule::getRemainingSpeed ) );

        // Move in space to (0, 5).
        movement = mover.onModule( ModuleType.MOBILITY, 0, module -> module.movement(
                staticGame.getLevel( LevelType.SPACE ).getTile( Vec2.create( 0, 5 ) ).get() ) );
        assertTrue( movement.isPossible() );
        assertEquals( 10d, movement.getCost() );
        movement.execute();
        assertEquals( LevelType.SPACE, mover.getLocation().get().getLevel().getType() );
        assertEquals( Vec2.create( 0, 5 ), mover.getLocation().get().getPosition() );
        assertEquals( 3d, mover.onModule( ModuleType.MOBILITY, 0, MobilityModule::getRemainingSpeed ) );

        // Move to ground & move to (1, 5).
        movement = mover.onModule( ModuleType.MOBILITY, 0, module -> module.movement(
                staticGame.getLevel( LevelType.GROUND ).getTile( Vec2.create( 1, 5 ) ).get() ) );
        assertFalse( movement.isPossible() );
        try {
            movement.execute();
            assertFalse( true );
        }
        catch (final Module.ImpossibleException ignored) {
        }
        assertEquals( LevelType.SPACE, mover.getLocation().get().getLevel().getType() );
        assertEquals( Vec2.create( 0, 5 ), mover.getLocation().get().getPosition() );
        assertEquals( 3d, mover.onModule( ModuleType.MOBILITY, 0, MobilityModule::getRemainingSpeed ) );
    }
}
