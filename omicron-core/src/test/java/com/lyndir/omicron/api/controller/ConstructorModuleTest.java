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

import static com.lyndir.omicron.api.util.TestUtils.*;
import static org.testng.Assert.*;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.system.util.ObjectMeta;
import com.lyndir.omicron.api.model.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


/**
 * @author lhunath, 2013-08-03
 */
public class ConstructorModuleTest {

    @ObjectMeta(ignoreFor = ObjectMeta.For.all)
    final Logger logger = Logger.get( getClass() );

    @BeforeMethod
    public void setUp()
            throws Exception {

        init();
    }

    @Test
    public void testOnNewTurn()
            throws Exception {

        // Create a unit that can build BASE & MOBILITY.
        ConstructorModule initBaseConstructorModule = new ConstructorModule( 3, ModuleType.BASE );
        ConstructorModule initMobilityConstructorModule = new ConstructorModule( 2, ModuleType.MOBILITY );
        PlayerObject baseMobilityConstructorUnit = createUnit( testUnitType( initBaseConstructorModule, initMobilityConstructorModule ), 0,
                                                               0 );
        staticGame.getController().start();

        // Create a BASE unit type and a BASE & MOBILITY unit type.
        UnitType baseUnit = testUnitType( 5, new BaseModule( 1, 1, 1, ImmutableSet.<LevelType>of() ) );
        UnitType baseMobilityUnit = testUnitType( 5, new BaseModule( 1, 1, 1, ImmutableSet.<LevelType>of() ),
                                                  new MobilityModule( 1, ImmutableMap.<LevelType, Double>of(),
                                                                      ImmutableMap.<LevelType, Double>of() ) );

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
        staticGame.getController().setReady( staticPlayer );
        assertEquals( baseConstructorModule.getRemainingSpeed(), 0 );
        assertEquals( mobilityConstructorModule.getRemainingSpeed(), 2 );
        assertEquals( site1.getRemainingComplexity( ModuleType.BASE ), 2 );
        assertEquals( site1.getRemainingComplexity( ModuleType.MOBILITY ), 0 );
        assertEquals( site1.getLocation().getContents().get(), site1 );
        staticGame.getController().setReady( staticPlayer );
        assertEquals( baseConstructorModule.getRemainingSpeed(), 1 );
        assertEquals( mobilityConstructorModule.getRemainingSpeed(), 2 );
        assertEquals( site1.getRemainingComplexity( ModuleType.BASE ), 0 );
        assertEquals( site1.getRemainingComplexity( ModuleType.MOBILITY ), 0 );
        assertNotEquals( site1.getLocation().getContents().get(), site1 );
        assertEquals( site1.getLocation().getContents().get().getType(), baseUnit );
        staticGame.getController().setReady( staticPlayer );

        // Build a BASE & MOBILITY unit, complexity = 5 so should take 2 turns at a speed of 3 for BASE and 3 turns at a speed of 2 for MOBILITY, so after 3 turns we should be done.
        Tile location2 = baseMobilityConstructorUnit.getLocation().neighbour( Coordinate.Side.W );
        ConstructorModule.ConstructionSite site2 = baseConstructorModule.schedule( baseMobilityUnit, location2 );
        assertEquals( baseConstructorModule.getRemainingSpeed(), 3 );
        assertEquals( mobilityConstructorModule.getRemainingSpeed(), 2 );
        assertEquals( site2.getRemainingComplexity( ModuleType.BASE ), 5 );
        assertEquals( site2.getRemainingComplexity( ModuleType.MOBILITY ), 5 );
        assertEquals( site2.getLocation().getContents().get(), site2 );
        staticGame.getController().setReady( staticPlayer );
        assertEquals( baseConstructorModule.getRemainingSpeed(), 0 );
        assertEquals( mobilityConstructorModule.getRemainingSpeed(), 0 );
        assertEquals( site2.getRemainingComplexity( ModuleType.BASE ), 2 );
        assertEquals( site2.getRemainingComplexity( ModuleType.MOBILITY ), 3 );
        assertEquals( site2.getLocation().getContents().get(), site2 );
        staticGame.getController().setReady( staticPlayer );
        assertEquals( baseConstructorModule.getRemainingSpeed(), 1 );
        assertEquals( mobilityConstructorModule.getRemainingSpeed(), 0 );
        assertEquals( site2.getRemainingComplexity( ModuleType.BASE ), 0 );
        assertEquals( site2.getRemainingComplexity( ModuleType.MOBILITY ), 1 );
        assertEquals( site2.getLocation().getContents().get(), site2 );
        staticGame.getController().setReady( staticPlayer );
        assertEquals( baseConstructorModule.getRemainingSpeed(), 3 );
        assertEquals( mobilityConstructorModule.getRemainingSpeed(), 1 );
        assertEquals( site2.getRemainingComplexity( ModuleType.BASE ), 0 );
        assertEquals( site2.getRemainingComplexity( ModuleType.MOBILITY ), 0 );
        assertNotEquals( site2.getLocation().getContents().get(), site2 );
        assertEquals( site2.getLocation().getContents().get().getType(), baseMobilityUnit );

        // Build a BASE & MOBILITY unit, using a constructor that can do only BASE, then help out with one that can do only MOBILITY.
        initBaseConstructorModule = new ConstructorModule( 3, ModuleType.BASE );
        PlayerObject baseConstructorUnit = createUnit( testUnitType( initBaseConstructorModule ), 10, 10 );
        baseConstructorModule = baseConstructorUnit.getModule( ModuleType.CONSTRUCTOR, 0 ).get();
        staticGame.getController().setReady( staticPlayer );

        Tile location3 = baseConstructorUnit.getLocation().neighbour( Coordinate.Side.E );
        ConstructorModule.ConstructionSite site3 = baseConstructorModule.schedule( baseMobilityUnit, location3 );
        assertEquals( baseConstructorModule.getRemainingSpeed(), 3 );
        assertEquals( site3.getRemainingComplexity( ModuleType.BASE ), 5 );
        assertEquals( site3.getRemainingComplexity( ModuleType.MOBILITY ), 5 );
        assertEquals( site3.getLocation().getContents().get(), site3 );
        staticGame.getController().setReady( staticPlayer );
        assertEquals( baseConstructorModule.getRemainingSpeed(), 0 );
        assertEquals( site3.getRemainingComplexity( ModuleType.BASE ), 2 );
        assertEquals( site3.getRemainingComplexity( ModuleType.MOBILITY ), 5 );
        assertEquals( site3.getLocation().getContents().get(), site3 );
        staticGame.getController().setReady( staticPlayer );
        assertEquals( baseConstructorModule.getRemainingSpeed(), 1 );
        assertEquals( site3.getRemainingComplexity( ModuleType.BASE ), 0 );
        assertEquals( site3.getRemainingComplexity( ModuleType.MOBILITY ), 5 );
        assertEquals( site3.getLocation().getContents().get(), site3 );
        staticGame.getController().setReady( staticPlayer );
        assertEquals( baseConstructorModule.getRemainingSpeed(), 3 );
        assertEquals( site3.getRemainingComplexity( ModuleType.BASE ), 0 );
        assertEquals( site3.getRemainingComplexity( ModuleType.MOBILITY ), 5 );
        assertEquals( site3.getLocation().getContents().get(), site3 );

        // Send help.
        initMobilityConstructorModule = new ConstructorModule( 2, ModuleType.MOBILITY );
        PlayerObject mobilityConstructorUnit = createUnit( testUnitType( initMobilityConstructorModule ), 10, 11 );
        mobilityConstructorModule = mobilityConstructorUnit.getModule( ModuleType.CONSTRUCTOR, 0 ).get();
        logger.dbg( "setReady" );
        staticGame.getController().setReady( staticPlayer );
        assertEquals( baseConstructorModule.getRemainingSpeed(), 3 );
        assertEquals( mobilityConstructorModule.getRemainingSpeed(), 2 );
        assertEquals( site3.getRemainingComplexity( ModuleType.BASE ), 0 );
        assertEquals( site3.getRemainingComplexity( ModuleType.MOBILITY ), 5 );
        assertEquals( site3.getLocation().getContents().get(), site3 );
        logger.dbg( "setReady" );
        staticGame.getController().setReady( staticPlayer );
        assertEquals( baseConstructorModule.getRemainingSpeed(), 3 );
        assertEquals( mobilityConstructorModule.getRemainingSpeed(), 0 );
        assertEquals( site3.getRemainingComplexity( ModuleType.BASE ), 0 );
        assertEquals( site3.getRemainingComplexity( ModuleType.MOBILITY ), 3 );
        assertEquals( site3.getLocation().getContents().get(), site3 );
        staticGame.getController().setReady( staticPlayer );
        assertEquals( baseConstructorModule.getRemainingSpeed(), 3 );
        assertEquals( mobilityConstructorModule.getRemainingSpeed(), 0 );
        assertEquals( site3.getRemainingComplexity( ModuleType.BASE ), 0 );
        assertEquals( site3.getRemainingComplexity( ModuleType.MOBILITY ), 1 );
        assertEquals( site3.getLocation().getContents().get(), site3 );
        staticGame.getController().setReady( staticPlayer );
        assertEquals( baseConstructorModule.getRemainingSpeed(), 3 );
        assertEquals( mobilityConstructorModule.getRemainingSpeed(), 1 );
        assertEquals( site3.getRemainingComplexity( ModuleType.BASE ), 0 );
        assertEquals( site3.getRemainingComplexity( ModuleType.MOBILITY ), 0 );
        assertNotEquals( site3.getLocation().getContents().get(), site3 );
        assertEquals( site3.getLocation().getContents().get().getType(), baseMobilityUnit );
        staticGame.getController().setReady( staticPlayer );
    }
}
