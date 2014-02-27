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

package com.lyndir.omicron.api.model;

import static org.testng.Assert.*;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.lyndir.lhunath.opal.math.Side;
import com.lyndir.lhunath.opal.system.util.CollectionUtils;
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
                                                                       .buildsModule( ModuleType.BASE );
        ConstructorModule initMobilityConstructorModule = ConstructorModule.createWithStandardResourceCost()
                                                                           .buildSpeed( 2 )
                                                                           .buildsModule( ModuleType.MOBILITY );
        GameObject baseMobilityConstructorUnit = createUnit( testUnitType( "Base Mobility Constructor",
                                                                           BaseModule.createWithStandardResourceCost()
                                                                                     .maxHealth( 1 )
                                                                                     .armor( 1 )
                                                                                     .viewRange( 1 )
                                                                                     .supportedLayers( LevelType.values() ),
                                                                           initBaseConstructorModule, initMobilityConstructorModule ), //
                                                             5, 5 );
        staticGame.getController().setReady();

        // Create a BASE unit type and a BASE & MOBILITY unit type.
        UnitType baseUnit = testUnitType( "Base Unit", 5, BaseModule.createWithStandardResourceCost()
                                                                    .maxHealth( 1 )
                                                                    .armor( 1 )
                                                                    .viewRange( 1 )
                                                                    .supportedLayers( ImmutableSet.<LevelType>of() ) );
        UnitType baseMobilityUnit = testUnitType( "Mobility Unit", 7, BaseModule.createWithStandardResourceCost()
                                                                                .maxHealth( 1 )
                                                                                .armor( 1 )
                                                                                .viewRange( 1 )
                                                                                .supportedLayers( ImmutableSet.<LevelType>of() ),
                                                  MobilityModule.createWithStandardResourceCost()
                                                                .movementSpeed( 1 )
                                                                .movementCost( ImmutableMap.<LevelType, Double>of() )
                                                                .levelingCost( ImmutableMap.<LevelType, Double>of() ) );

        // Build a BASE unit, initially without resources.
        ConstructorModule baseConstructorModule = baseMobilityConstructorUnit.getModule( ModuleType.CONSTRUCTOR, 0 ).get();
        ConstructorModule mobilityConstructorModule = baseMobilityConstructorUnit.getModule( ModuleType.CONSTRUCTOR, 1 ).get();
        Tile location1 = baseMobilityConstructorUnit.getLocation().get().neighbour( Side.E ).get();
        ConstructorModule.ConstructionSite site1 = baseConstructorModule.schedule( baseUnit, location1 );
        assertEquals( baseConstructorModule.getRemainingSpeed(), 3 );
        assertEquals( mobilityConstructorModule.getRemainingSpeed(), 2 );
        assertEquals( site1.getRemainingWork( ModuleType.BASE ), 5 );
        assertEquals( site1.getRemainingWork( ModuleType.MOBILITY ), 0 );
        assertEquals( site1.getLocation().get().checkContents().get(), site1 );
        assertTrue( CollectionUtils.isEqualElements( staticPlayer.getObjects(), ImmutableSet.of( baseMobilityConstructorUnit, site1 ) ) );

        staticGame.getController().setReady();
        assertEquals( baseConstructorModule.getRemainingSpeed(), 3 );
        assertEquals( mobilityConstructorModule.getRemainingSpeed(), 2 );
        assertEquals( site1.getRemainingWork( ModuleType.BASE ), 5 );
        assertEquals( site1.getRemainingWork( ModuleType.MOBILITY ), 0 );
        assertEquals( site1.getLocation().get().checkContents().get(), site1 );
        assertTrue( CollectionUtils.isEqualElements( staticPlayer.getObjects(), ImmutableSet.of( baseMobilityConstructorUnit, site1 ) ) );

        // Now provide resources, work = 5 so should take 2 turns at a speed of 3.
        GameObject containerUnit = createUnit( testUnitType( "Metal Container", ContainerModule.createWithStandardResourceCost()
                                                                                               .resourceType( ResourceType.METALS )
                                                                                               .capacity( 100 ) ), 5, 6 );
        int metals = baseUnit.getConstructionWork() * ModuleType.BASE.getStandardCost().get( ResourceType.METALS );
        containerUnit.onModule( ModuleType.CONTAINER, 0 ).addStock( metals );
        staticGame.getController().setReady();
        assertEquals( baseConstructorModule.getRemainingSpeed(), 0 );
        assertEquals( mobilityConstructorModule.getRemainingSpeed(), 2 );
        assertEquals( site1.getRemainingWork( ModuleType.BASE ), 2 );
        assertEquals( site1.getRemainingWork( ModuleType.MOBILITY ), 0 );
        assertEquals( site1.getLocation().get().checkContents().get(), site1 );
        assertTrue( CollectionUtils.isEqualElements( staticPlayer.getObjects(),
                                                     ImmutableSet.of( baseMobilityConstructorUnit, site1, containerUnit ) ) );

        staticGame.getController().setReady();
        assertEquals( baseConstructorModule.getRemainingSpeed(), 1 );
        assertEquals( mobilityConstructorModule.getRemainingSpeed(), 2 );
        assertEquals( site1.getRemainingWork( ModuleType.BASE ), 0 );
        assertEquals( site1.getRemainingWork( ModuleType.MOBILITY ), 0 );
        assertNotEquals( site1.getLocation().get().checkContents().get(), site1 );
        GameObject newUnit1 = site1.getLocation().get().checkContents().get();
        assertEquals( newUnit1.getType(), baseUnit );
        assertTrue( CollectionUtils.isEqualElements( staticPlayer.getObjects(),
                                                     ImmutableSet.of( baseMobilityConstructorUnit, newUnit1, containerUnit ) ) );
        assertEquals( containerUnit.getModule( ModuleType.CONTAINER, 0 ).get().getStock(), 0 );
        newUnit1.getController().die();
        assertTrue( CollectionUtils.isEqualElements( staticPlayer.getObjects(),
                                                     ImmutableSet.of( baseMobilityConstructorUnit, containerUnit ) ) );

        // Build a BASE & MOBILITY unit, work = 7 so should take 3 turns at a speed of 3 for BASE and 4 turns at a speed of 2 for MOBILITY, so after 4 turns we should be done.
        metals = baseMobilityUnit.getConstructionWork() * (ModuleType.BASE.getStandardCost().get( ResourceType.METALS ) + //
                                                           ModuleType.MOBILITY.getStandardCost().get( ResourceType.METALS ));
        containerUnit.onModule( ModuleType.CONTAINER, 0 ).addStock( metals );
        staticGame.getController().setReady();
        Tile location2 = baseMobilityConstructorUnit.getLocation().get().neighbour( Side.W ).get();
        ConstructorModule.ConstructionSite site2 = baseConstructorModule.schedule( baseMobilityUnit, location2 );
        assertEquals( baseConstructorModule.getRemainingSpeed(), 3 );
        assertEquals( mobilityConstructorModule.getRemainingSpeed(), 2 );
        assertEquals( site2.getRemainingWork( ModuleType.BASE ), 7 );
        assertEquals( site2.getRemainingWork( ModuleType.MOBILITY ), 7 );
        assertEquals( site2.getLocation().get().checkContents().get(), site2 );
        assertTrue( CollectionUtils.isEqualElements( staticPlayer.getObjects(),
                                                     ImmutableSet.of( baseMobilityConstructorUnit, containerUnit, site2 ) ) );

        staticGame.getController().setReady();
        assertEquals( baseConstructorModule.getRemainingSpeed(), 0 );
        assertEquals( mobilityConstructorModule.getRemainingSpeed(), 0 );
        assertEquals( site2.getRemainingWork( ModuleType.BASE ), 4 );
        assertEquals( site2.getRemainingWork( ModuleType.MOBILITY ), 5 );
        assertEquals( site2.getLocation().get().checkContents().get(), site2 );
        assertTrue( CollectionUtils.isEqualElements( staticPlayer.getObjects(),
                                                     ImmutableSet.of( baseMobilityConstructorUnit, containerUnit, site2 ) ) );

        staticGame.getController().setReady();
        assertEquals( baseConstructorModule.getRemainingSpeed(), 0 );
        assertEquals( mobilityConstructorModule.getRemainingSpeed(), 0 );
        assertEquals( site2.getRemainingWork( ModuleType.BASE ), 1 );
        assertEquals( site2.getRemainingWork( ModuleType.MOBILITY ), 3 );
        assertEquals( site2.getLocation().get().checkContents().get(), site2 );
        assertTrue( CollectionUtils.isEqualElements( staticPlayer.getObjects(),
                                                     ImmutableSet.of( baseMobilityConstructorUnit, containerUnit, site2 ) ) );

        staticGame.getController().setReady();
        assertEquals( baseConstructorModule.getRemainingSpeed(), 2 );
        assertEquals( mobilityConstructorModule.getRemainingSpeed(), 0 );
        assertEquals( site2.getRemainingWork( ModuleType.BASE ), 0 );
        assertEquals( site2.getRemainingWork( ModuleType.MOBILITY ), 1 );
        assertEquals( site2.getLocation().get().checkContents().get(), site2 );
        assertTrue( CollectionUtils.isEqualElements( staticPlayer.getObjects(),
                                                     ImmutableSet.of( baseMobilityConstructorUnit, containerUnit, site2 ) ) );

        staticGame.getController().setReady();
        assertEquals( baseConstructorModule.getRemainingSpeed(), 3 );
        assertEquals( mobilityConstructorModule.getRemainingSpeed(), 1 );
        assertEquals( site2.getRemainingWork( ModuleType.BASE ), 0 );
        assertEquals( site2.getRemainingWork( ModuleType.MOBILITY ), 0 );
        assertNotEquals( site2.getLocation().get().checkContents().get(), site2 );
        GameObject newUnit2 = site2.getLocation().get().checkContents().get();
        assertEquals( newUnit2.getType(), baseMobilityUnit );
        assertTrue( CollectionUtils.isEqualElements( staticPlayer.getObjects(),
                                                     ImmutableSet.of( baseMobilityConstructorUnit, containerUnit, newUnit2 ) ) );
        assertEquals( containerUnit.getModule( ModuleType.CONTAINER, 0 ).get().getStock(), 0 );
        newUnit2.getController().die();
        baseMobilityConstructorUnit.getController().die();
        containerUnit.getController().die();
        assertTrue( CollectionUtils.isEqualElements( staticPlayer.getObjects(), ImmutableSet.of() ) );

        // Build a BASE & MOBILITY unit, using a constructor that can do only BASE, then help out with one that can do only MOBILITY.
        initBaseConstructorModule = ConstructorModule.createWithStandardResourceCost().buildSpeed( 3 ).buildsModule( ModuleType.BASE );
        GameObject baseConstructorUnit = createUnit( testUnitType( "Base Constructor", BaseModule.createWithStandardResourceCost()
                                                                                                 .maxHealth( 1 )
                                                                                                 .armor( 1 )
                                                                                                 .viewRange( 1 )
                                                                                                 .supportedLayers( LevelType.values() ),
                                                                   initBaseConstructorModule ), 5, 5 );
        baseConstructorModule = baseConstructorUnit.getModule( ModuleType.CONSTRUCTOR, 0 ).get();
        containerUnit = createUnit( testUnitType( "Metal Container", ContainerModule.createWithStandardResourceCost()
                                                                                    .resourceType( ResourceType.METALS )
                                                                                    .capacity( 100 ) ), 5, 4 );
        metals = baseMobilityUnit.getConstructionWork() * (ModuleType.BASE.getStandardCost().get( ResourceType.METALS ) + //
                                                           ModuleType.MOBILITY.getStandardCost().get( ResourceType.METALS ));
        containerUnit.onModule( ModuleType.CONTAINER, 0 ).addStock( metals );
        staticGame.getController().setReady();
        Tile location3 = baseConstructorUnit.getLocation().get().neighbour( Side.E ).get();
        ConstructorModule.ConstructionSite site3 = baseConstructorModule.schedule( baseMobilityUnit, location3 );
        assertEquals( baseConstructorModule.getRemainingSpeed(), 3 );
        assertEquals( site3.getRemainingWork( ModuleType.BASE ), 7 );
        assertEquals( site3.getRemainingWork( ModuleType.MOBILITY ), 7 );
        assertEquals( site3.getLocation().get().checkContents().get(), site3 );
        assertTrue( CollectionUtils.isEqualElements( staticPlayer.getObjects(),
                                                     ImmutableSet.of( baseConstructorUnit, containerUnit, site3 ) ) );

        staticGame.getController().setReady();
        assertEquals( baseConstructorModule.getRemainingSpeed(), 0 );
        assertEquals( site3.getRemainingWork( ModuleType.BASE ), 4 );
        assertEquals( site3.getRemainingWork( ModuleType.MOBILITY ), 7 );
        assertEquals( site3.getLocation().get().checkContents().get(), site3 );
        assertTrue( CollectionUtils.isEqualElements( staticPlayer.getObjects(),
                                                     ImmutableSet.of( baseConstructorUnit, containerUnit, site3 ) ) );

        staticGame.getController().setReady();
        assertEquals( baseConstructorModule.getRemainingSpeed(), 0 );
        assertEquals( site3.getRemainingWork( ModuleType.BASE ), 1 );
        assertEquals( site3.getRemainingWork( ModuleType.MOBILITY ), 7 );
        assertEquals( site3.getLocation().get().checkContents().get(), site3 );
        assertTrue( CollectionUtils.isEqualElements( staticPlayer.getObjects(),
                                                     ImmutableSet.of( baseConstructorUnit, containerUnit, site3 ) ) );

        staticGame.getController().setReady();
        assertEquals( baseConstructorModule.getRemainingSpeed(), 2 );
        assertEquals( site3.getRemainingWork( ModuleType.BASE ), 0 );
        assertEquals( site3.getRemainingWork( ModuleType.MOBILITY ), 7 );
        assertEquals( site3.getLocation().get().checkContents().get(), site3 );
        assertTrue( CollectionUtils.isEqualElements( staticPlayer.getObjects(),
                                                     ImmutableSet.of( baseConstructorUnit, containerUnit, site3 ) ) );

        staticGame.getController().setReady();
        assertEquals( baseConstructorModule.getRemainingSpeed(), 3 );
        assertEquals( site3.getRemainingWork( ModuleType.BASE ), 0 );
        assertEquals( site3.getRemainingWork( ModuleType.MOBILITY ), 7 );
        assertEquals( site3.getLocation().get().checkContents().get(), site3 );
        assertTrue( CollectionUtils.isEqualElements( staticPlayer.getObjects(),
                                                     ImmutableSet.of( baseConstructorUnit, containerUnit, site3 ) ) );

        // Send help.
        initMobilityConstructorModule = ConstructorModule.createWithStandardResourceCost()
                                                         .buildSpeed( 2 )
                                                         .buildsModule( ModuleType.MOBILITY );
        IGameObject mobilityConstructorUnit = createUnit( testUnitType( "Mobility Constructor", initMobilityConstructorModule ), 5, 6 );
        mobilityConstructorModule = mobilityConstructorUnit.getModule( ModuleType.CONSTRUCTOR, 0 ).get();
        staticGame.getController().setReady();
        assertEquals( baseConstructorModule.getRemainingSpeed(), 3 );
        assertEquals( mobilityConstructorModule.getRemainingSpeed(), 2 );
        assertEquals( site3.getRemainingWork( ModuleType.BASE ), 0 );
        assertEquals( site3.getRemainingWork( ModuleType.MOBILITY ), 7 );
        assertEquals( site3.getLocation().get().checkContents().get(), site3 );
        assertTrue( CollectionUtils.isEqualElements( staticPlayer.getObjects(),
                                                     ImmutableSet.of( baseConstructorUnit, containerUnit, mobilityConstructorUnit,
                                                                      site3 ) ) );

        mobilityConstructorModule.setTarget( baseConstructorUnit );
        staticGame.getController().setReady();
        assertEquals( baseConstructorModule.getRemainingSpeed(), 3 );
        assertEquals( mobilityConstructorModule.getRemainingSpeed(), 0 );
        assertEquals( site3.getRemainingWork( ModuleType.BASE ), 0 );
        assertEquals( site3.getRemainingWork( ModuleType.MOBILITY ), 5 );
        assertEquals( site3.getLocation().get().checkContents().get(), site3 );
        assertTrue( CollectionUtils.isEqualElements( staticPlayer.getObjects(),
                                                     ImmutableSet.of( baseConstructorUnit, containerUnit, mobilityConstructorUnit,
                                                                      site3 ) ) );

        staticGame.getController().setReady();
        assertEquals( baseConstructorModule.getRemainingSpeed(), 3 );
        assertEquals( mobilityConstructorModule.getRemainingSpeed(), 0 );
        assertEquals( site3.getRemainingWork( ModuleType.BASE ), 0 );
        assertEquals( site3.getRemainingWork( ModuleType.MOBILITY ), 3 );
        assertEquals( site3.getLocation().get().checkContents().get(), site3 );
        assertTrue( CollectionUtils.isEqualElements( staticPlayer.getObjects(),
                                                     ImmutableSet.of( baseConstructorUnit, containerUnit, mobilityConstructorUnit,
                                                                      site3 ) ) );

        staticGame.getController().setReady();
        assertEquals( baseConstructorModule.getRemainingSpeed(), 3 );
        assertEquals( mobilityConstructorModule.getRemainingSpeed(), 0 );
        assertEquals( site3.getRemainingWork( ModuleType.BASE ), 0 );
        assertEquals( site3.getRemainingWork( ModuleType.MOBILITY ), 1 );
        assertEquals( site3.getLocation().get().checkContents().get(), site3 );
        assertTrue( CollectionUtils.isEqualElements( staticPlayer.getObjects(),
                                                     ImmutableSet.of( baseConstructorUnit, containerUnit, mobilityConstructorUnit,
                                                                      site3 ) ) );

        staticGame.getController().setReady();
        assertEquals( baseConstructorModule.getRemainingSpeed(), 3 );
        assertEquals( mobilityConstructorModule.getRemainingSpeed(), 1 );
        assertEquals( site3.getRemainingWork( ModuleType.BASE ), 0 );
        assertEquals( site3.getRemainingWork( ModuleType.MOBILITY ), 0 );
        assertNotEquals( site3.getLocation().get().checkContents().get(), site3 );
        GameObject newUnit3 = site3.getLocation().get().checkContents().get();
        assertEquals( newUnit3.getType(), baseMobilityUnit );
        assertTrue( CollectionUtils.isEqualElements( staticPlayer.getObjects(),
                                                     ImmutableSet.of( baseConstructorUnit, containerUnit, mobilityConstructorUnit,
                                                                      newUnit3 ) ) );
        assertEquals( containerUnit.getModule( ModuleType.CONTAINER, 0 ).get().getStock(), 0 );
        newUnit3.getController().die();
        assertTrue( CollectionUtils.isEqualElements( staticPlayer.getObjects(),
                                                     ImmutableSet.of( baseConstructorUnit, containerUnit, mobilityConstructorUnit ) ) );
    }
}
