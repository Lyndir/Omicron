package com.lyndir.omicron.api;

import static org.testng.Assert.*;

import org.testng.annotations.Test;


/**
 * @author lhunath, 2013-07-16
 */
public class ExtractorModuleTest extends AbstractTest {

    @Test
    public void testOnNewTurn()
            throws Exception {

        // Create an extractor unit on a tile with fuel.
        ExtractorModule extractorModule = ExtractorModule.createWithStandardResourceCost().resourceType( ResourceType.FUEL ).speed( 5 );
        ContainerModule unconnectedContainerModule = ContainerModule.createWithStandardResourceCost()
                                                                    .resourceType( ResourceType.FUEL )
                                                                    .capacity( 3 );
        createUnit( testUnitType( "Extractor", extractorModule ), 0, 0 );
        createUnit( testUnitType( "Unconnected Container", unconnectedContainerModule ), 3, 0 );
        extractorModule.getGameObject().getLocation().setResourceQuantity( ResourceType.FUEL, 10 );

        // There is no connected container yet, mining should fail.
        assertEquals( (int) extractorModule.getGameObject().getLocation().getResourceQuantity( ResourceType.FUEL ).get(), 10 );
        extractorModule.onNewTurn();
        assertEquals( (int) extractorModule.getGameObject().getLocation().getResourceQuantity( ResourceType.FUEL ).get(), 10 );

        // Create a connected container unit.
        ContainerModule smallConnectedContainerModule = ContainerModule.createWithStandardResourceCost()
                                                                       .resourceType( ResourceType.FUEL )
                                                                       .capacity( 3 );
        createUnit( testUnitType( "Small Connected Container", smallConnectedContainerModule ), 1, 0 );

        // Now we should be able to mine enough to fill the container.
        assertEquals( smallConnectedContainerModule.getAvailable(), 3 );
        extractorModule.onNewTurn();
        assertEquals( (int) extractorModule.getGameObject().getLocation().getResourceQuantity( ResourceType.FUEL ).get(), 7 );
        assertEquals( smallConnectedContainerModule.getAvailable(), 0 );
        extractorModule.onNewTurn();
        assertEquals( (int) extractorModule.getGameObject().getLocation().getResourceQuantity( ResourceType.FUEL ).get(), 7 );
        assertEquals( smallConnectedContainerModule.getAvailable(), 0 );

        // Create a bigger connected container unit.
        ContainerModule bigConnectedContainerModule = ContainerModule.createWithStandardResourceCost()
                                                                     .resourceType( ResourceType.FUEL )
                                                                     .capacity( 15 );
        createUnit( testUnitType( "Big Connected Container", bigConnectedContainerModule ), 0, 1 );

        // Now we should be able to mine enough to max out the extractor's speed and then empty the tile.
        assertEquals( smallConnectedContainerModule.getAvailable(), 0 );
        assertEquals( bigConnectedContainerModule.getAvailable(), 15 );
        extractorModule.onNewTurn();
        assertEquals( (int) extractorModule.getGameObject().getLocation().getResourceQuantity( ResourceType.FUEL ).get(), 2 );
        assertEquals( bigConnectedContainerModule.getAvailable(), 10 );
        extractorModule.onNewTurn();
        assertFalse( extractorModule.getGameObject().getLocation().getResourceQuantity( ResourceType.FUEL ).isPresent() );
        assertEquals( bigConnectedContainerModule.getAvailable(), 8 );
        assertEquals( bigConnectedContainerModule.getStock(), 7 );
    }
}
