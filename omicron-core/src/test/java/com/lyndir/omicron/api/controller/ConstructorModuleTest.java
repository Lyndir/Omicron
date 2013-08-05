/*
 * Copyright 2010, Maarten Billemont
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.lyndir.omicron.api.controller;

import static org.testng.Assert.*;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.lyndir.lhunath.opal.system.util.CollectionUtils;
import com.lyndir.omicron.api.model.*;
import com.lyndir.omicron.api.util.AbstractTest;
import org.testng.annotations.Test;


/**
 * @author lhunath, 2013-08-03
 */
public class ConstructorModuleTest extends AbstractTest {

    @Test
    public void testOnNewTurn()
            throws Exception {

        // Create a unit that can build BASE & MOBILITY.
        ConstructorModule initBaseConstructorModule = ConstructorModule.createWithStandardResourceCost()
                                                                       .buildSpeed( 3 )
                                                                       .supportedLayers( ModuleType.BASE );
        ConstructorModule initMobilityConstructorModule = ConstructorModule.createWithStandardResourceCost()
                                                                           .buildSpeed( 2 )
                                                                           .supportedLayers( ModuleType.MOBILITY );
        PlayerObject baseMobilityConstructorUnit = createUnit(
                testUnitType( "Base Mobility Constructor", initBaseConstructorModule, initMobilityConstructorModule ), 0, 0 );
        staticGame.getController().start();

        // Create a BASE unit type and a BASE & MOBILITY unit type.
        UnitType baseUnit = testUnitType( "Base Unit", 5, BaseModule.createWithStandardResourceCost()
                                                                    .maxHealth( 1 )
                                                                    .armor( 1 )
                                                                    .viewRange( 1 )
                                                                    .supportedLayers( ImmutableSet.<LevelType>of() ) );
        UnitType baseMobilityUnit = testUnitType( "Mobility Unit", 5, BaseModule.createWithStandardResourceCost()
                                                                                .maxHealth( 1 )
                                                                                .armor( 1 )
                                                                                .viewRange( 1 )
                                                                                .supportedLayers( ImmutableSet.<LevelType>of() ),
                                                  MobilityModule.createWithStandardResourceCost()
                                                                .movementSpeed( 1 )
                                                                .movementCost( ImmutableMap.<LevelType, Double>of() )
                                                                .levelingCost( ImmutableMap.<LevelType, Double>of() ) );

        // Build a BASE unit, complexity = 5 so should take 2 turns at a speed of 3.
        ConstructorModule baseConstructorModule = baseMobilityConstructorUnit.getModule( ModuleType.CONSTRUCTOR, 0 ).get();
        ConstructorModule mobilityConstructorModule = baseMobilityConstructorUnit.getModule( ModuleType.CONSTRUCTOR, 1 ).get();
        Tile location1 = baseMobilityConstructorUnit.getLocation().neighbour( Coordinate.Side.E );
        ConstructorModule.ConstructionSite site1 = baseConstructorModule.schedule( baseUnit, location1 );
        assertEquals( baseConstructorModule.getRemainingSpeed(), 3 );
        assertEquals( mobilityConstructorModule.getRemainingSpeed(), 2 );
        assertEquals( site1.getRemainingComplexity( ModuleType.BASE ), 5 );
        assertEquals( site1.getRemainingComplexity( ModuleType.MOBILITY ), 0 );
        assertEquals( site1.getLocation().getContents().get(), site1 );
        assertTrue( CollectionUtils.isEqualElements( staticPlayer.getObjects(), ImmutableSet.of( baseMobilityConstructorUnit, site1 ) ) );

        staticGame.getController().setReady( staticPlayer );
        assertEquals( baseConstructorModule.getRemainingSpeed(), 0 );
        assertEquals( mobilityConstructorModule.getRemainingSpeed(), 2 );
        assertEquals( site1.getRemainingComplexity( ModuleType.BASE ), 2 );
        assertEquals( site1.getRemainingComplexity( ModuleType.MOBILITY ), 0 );
        assertEquals( site1.getLocation().getContents().get(), site1 );
        assertTrue( CollectionUtils.isEqualElements( staticPlayer.getObjects(), ImmutableSet.of( baseMobilityConstructorUnit, site1 ) ) );

        staticGame.getController().setReady( staticPlayer );
        assertEquals( baseConstructorModule.getRemainingSpeed(), 1 );
        assertEquals( mobilityConstructorModule.getRemainingSpeed(), 2 );
        assertEquals( site1.getRemainingComplexity( ModuleType.BASE ), 0 );
        assertEquals( site1.getRemainingComplexity( ModuleType.MOBILITY ), 0 );
        assertNotEquals( site1.getLocation().getContents().get(), site1 );
        GameObject newUnit1 = site1.getLocation().getContents().get();
        assertEquals( newUnit1.getType(), baseUnit );
        assertTrue(
                CollectionUtils.isEqualElements( staticPlayer.getObjects(), ImmutableSet.of( baseMobilityConstructorUnit, newUnit1 ) ) );
        newUnit1.getController().die();
        assertTrue( CollectionUtils.isEqualElements( staticPlayer.getObjects(), ImmutableSet.of( baseMobilityConstructorUnit ) ) );

        // Build a BASE & MOBILITY unit, complexity = 5 so should take 2 turns at a speed of 3 for BASE and 3 turns at a speed of 2 for MOBILITY, so after 3 turns we should be done.
        staticGame.getController().setReady( staticPlayer );
        Tile location2 = baseMobilityConstructorUnit.getLocation().neighbour( Coordinate.Side.W );
        ConstructorModule.ConstructionSite site2 = baseConstructorModule.schedule( baseMobilityUnit, location2 );
        assertEquals( baseConstructorModule.getRemainingSpeed(), 3 );
        assertEquals( mobilityConstructorModule.getRemainingSpeed(), 2 );
        assertEquals( site2.getRemainingComplexity( ModuleType.BASE ), 5 );
        assertEquals( site2.getRemainingComplexity( ModuleType.MOBILITY ), 5 );
        assertEquals( site2.getLocation().getContents().get(), site2 );
        assertTrue( CollectionUtils.isEqualElements( staticPlayer.getObjects(), ImmutableSet.of( baseMobilityConstructorUnit, site2 ) ) );

        staticGame.getController().setReady( staticPlayer );
        assertEquals( baseConstructorModule.getRemainingSpeed(), 0 );
        assertEquals( mobilityConstructorModule.getRemainingSpeed(), 0 );
        assertEquals( site2.getRemainingComplexity( ModuleType.BASE ), 2 );
        assertEquals( site2.getRemainingComplexity( ModuleType.MOBILITY ), 3 );
        assertEquals( site2.getLocation().getContents().get(), site2 );
        assertTrue( CollectionUtils.isEqualElements( staticPlayer.getObjects(), ImmutableSet.of( baseMobilityConstructorUnit, site2 ) ) );

        staticGame.getController().setReady( staticPlayer );
        assertEquals( baseConstructorModule.getRemainingSpeed(), 1 );
        assertEquals( mobilityConstructorModule.getRemainingSpeed(), 0 );
        assertEquals( site2.getRemainingComplexity( ModuleType.BASE ), 0 );
        assertEquals( site2.getRemainingComplexity( ModuleType.MOBILITY ), 1 );
        assertEquals( site2.getLocation().getContents().get(), site2 );
        assertTrue( CollectionUtils.isEqualElements( staticPlayer.getObjects(), ImmutableSet.of( baseMobilityConstructorUnit, site2 ) ) );

        staticGame.getController().setReady( staticPlayer );
        assertEquals( baseConstructorModule.getRemainingSpeed(), 3 );
        assertEquals( mobilityConstructorModule.getRemainingSpeed(), 1 );
        assertEquals( site2.getRemainingComplexity( ModuleType.BASE ), 0 );
        assertEquals( site2.getRemainingComplexity( ModuleType.MOBILITY ), 0 );
        assertNotEquals( site2.getLocation().getContents().get(), site2 );
        GameObject newUnit2 = site2.getLocation().getContents().get();
        assertEquals( newUnit2.getType(), baseMobilityUnit );
        assertTrue(
                CollectionUtils.isEqualElements( staticPlayer.getObjects(), ImmutableSet.of( baseMobilityConstructorUnit, newUnit2 ) ) );
        newUnit2.getController().die();
        baseMobilityConstructorUnit.getController().die();
        assertTrue( CollectionUtils.isEqualElements( staticPlayer.getObjects(), ImmutableSet.of() ) );

        // Build a BASE & MOBILITY unit, using a constructor that can do only BASE, then help out with one that can do only MOBILITY.
        initBaseConstructorModule = ConstructorModule.createWithStandardResourceCost().buildSpeed( 3 ).supportedLayers( ModuleType.BASE );
        PlayerObject baseConstructorUnit = createUnit( testUnitType( "Base Constructor", initBaseConstructorModule ), 5, 5 );
        baseConstructorModule = baseConstructorUnit.getModule( ModuleType.CONSTRUCTOR, 0 ).get();
        staticGame.getController().setReady( staticPlayer );
        Tile location3 = baseConstructorUnit.getLocation().neighbour( Coordinate.Side.E );
        ConstructorModule.ConstructionSite site3 = baseConstructorModule.schedule( baseMobilityUnit, location3 );
        assertEquals( baseConstructorModule.getRemainingSpeed(), 3 );
        assertEquals( site3.getRemainingComplexity( ModuleType.BASE ), 5 );
        assertEquals( site3.getRemainingComplexity( ModuleType.MOBILITY ), 5 );
        assertEquals( site3.getLocation().getContents().get(), site3 );
        assertTrue( CollectionUtils.isEqualElements( staticPlayer.getObjects(), ImmutableSet.of( baseConstructorUnit, site3 ) ) );

        staticGame.getController().setReady( staticPlayer );
        assertEquals( baseConstructorModule.getRemainingSpeed(), 0 );
        assertEquals( site3.getRemainingComplexity( ModuleType.BASE ), 2 );
        assertEquals( site3.getRemainingComplexity( ModuleType.MOBILITY ), 5 );
        assertEquals( site3.getLocation().getContents().get(), site3 );
        assertTrue( CollectionUtils.isEqualElements( staticPlayer.getObjects(), ImmutableSet.of( baseConstructorUnit, site3 ) ) );

        staticGame.getController().setReady( staticPlayer );
        assertEquals( baseConstructorModule.getRemainingSpeed(), 1 );
        assertEquals( site3.getRemainingComplexity( ModuleType.BASE ), 0 );
        assertEquals( site3.getRemainingComplexity( ModuleType.MOBILITY ), 5 );
        assertEquals( site3.getLocation().getContents().get(), site3 );
        assertTrue( CollectionUtils.isEqualElements( staticPlayer.getObjects(), ImmutableSet.of( baseConstructorUnit, site3 ) ) );

        staticGame.getController().setReady( staticPlayer );
        assertEquals( baseConstructorModule.getRemainingSpeed(), 3 );
        assertEquals( site3.getRemainingComplexity( ModuleType.BASE ), 0 );
        assertEquals( site3.getRemainingComplexity( ModuleType.MOBILITY ), 5 );
        assertEquals( site3.getLocation().getContents().get(), site3 );
        assertTrue( CollectionUtils.isEqualElements( staticPlayer.getObjects(), ImmutableSet.of( baseConstructorUnit, site3 ) ) );

        // Send help.
        initMobilityConstructorModule = ConstructorModule.createWithStandardResourceCost()
                                                         .buildSpeed( 2 )
                                                         .supportedLayers( ModuleType.MOBILITY );
        PlayerObject mobilityConstructorUnit = createUnit( testUnitType( "Mobility Constructor", initMobilityConstructorModule ), 5, 6 );
        mobilityConstructorModule = mobilityConstructorUnit.getModule( ModuleType.CONSTRUCTOR, 0 ).get();
        staticGame.getController().setReady( staticPlayer );
        assertEquals( baseConstructorModule.getRemainingSpeed(), 3 );
        assertEquals( mobilityConstructorModule.getRemainingSpeed(), 2 );
        assertEquals( site3.getRemainingComplexity( ModuleType.BASE ), 0 );
        assertEquals( site3.getRemainingComplexity( ModuleType.MOBILITY ), 5 );
        assertEquals( site3.getLocation().getContents().get(), site3 );
        assertTrue( CollectionUtils.isEqualElements( staticPlayer.getObjects(),
                                                     ImmutableSet.of( baseConstructorUnit, mobilityConstructorUnit, site3 ) ) );

        mobilityConstructorModule.setTarget( baseConstructorUnit );
        staticGame.getController().setReady( staticPlayer );
        assertEquals( baseConstructorModule.getRemainingSpeed(), 3 );
        assertEquals( mobilityConstructorModule.getRemainingSpeed(), 0 );
        assertEquals( site3.getRemainingComplexity( ModuleType.BASE ), 0 );
        assertEquals( site3.getRemainingComplexity( ModuleType.MOBILITY ), 3 );
        assertEquals( site3.getLocation().getContents().get(), site3 );
        assertTrue( CollectionUtils.isEqualElements( staticPlayer.getObjects(),
                                                     ImmutableSet.of( baseConstructorUnit, mobilityConstructorUnit, site3 ) ) );

        staticGame.getController().setReady( staticPlayer );
        assertEquals( baseConstructorModule.getRemainingSpeed(), 3 );
        assertEquals( mobilityConstructorModule.getRemainingSpeed(), 0 );
        assertEquals( site3.getRemainingComplexity( ModuleType.BASE ), 0 );
        assertEquals( site3.getRemainingComplexity( ModuleType.MOBILITY ), 1 );
        assertEquals( site3.getLocation().getContents().get(), site3 );
        assertTrue( CollectionUtils.isEqualElements( staticPlayer.getObjects(),
                                                     ImmutableSet.of( baseConstructorUnit, mobilityConstructorUnit, site3 ) ) );

        staticGame.getController().setReady( staticPlayer );
        assertEquals( baseConstructorModule.getRemainingSpeed(), 3 );
        assertEquals( mobilityConstructorModule.getRemainingSpeed(), 1 );
        assertEquals( site3.getRemainingComplexity( ModuleType.BASE ), 0 );
        assertEquals( site3.getRemainingComplexity( ModuleType.MOBILITY ), 0 );
        assertNotEquals( site3.getLocation().getContents().get(), site3 );
        GameObject newUnit3 = site3.getLocation().getContents().get();
        assertEquals( newUnit3.getType(), baseMobilityUnit );
        assertTrue( CollectionUtils.isEqualElements( staticPlayer.getObjects(),
                                                     ImmutableSet.of( baseConstructorUnit, mobilityConstructorUnit, newUnit3 ) ) );
        newUnit3.getController().die();
        assertTrue( CollectionUtils.isEqualElements( staticPlayer.getObjects(),
                                                     ImmutableSet.of( baseConstructorUnit, mobilityConstructorUnit ) ) );
    }
}
