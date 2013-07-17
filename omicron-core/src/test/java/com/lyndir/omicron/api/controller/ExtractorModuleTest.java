package com.lyndir.omicron.api.controller;

import static org.testng.Assert.assertEquals;

import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.omicron.api.model.*;
import com.lyndir.omicron.api.util.TestUtils;
import org.testng.annotations.Test;


/**
 * @author lhunath, 2013-07-16
 */
public class ExtractorModuleTest {

    static final Logger logger = Logger.get( ExtractorModuleTest.class );

    @Test
    public void testOnNewTurn()
            throws Exception {

        // Create an extractor unit on a tile with fuel.
        ExtractorModule extractorModule = new ExtractorModule( ResourceType.FUEL, 5 );
        ContainerModule unconnectedContainerModule = new ContainerModule( ResourceType.FUEL, 3 );
        TestUtils.createObjectForModules( 0, 0, extractorModule );
        TestUtils.createObjectForModules( 3, 0, unconnectedContainerModule );
        extractorModule.getGameObject().getLocation().setResourceQuantity( ResourceType.FUEL, 10 );

        // There is no connected container yet, mining should fail.
        assertEquals( extractorModule.getGameObject().getLocation().getResourceQuantity( ResourceType.FUEL ), 10 );
        extractorModule.onNewTurn();
        assertEquals( extractorModule.getGameObject().getLocation().getResourceQuantity( ResourceType.FUEL ), 10 );

        // Create a connected container unit.
        ContainerModule smallConnectedContainerModule = new ContainerModule( ResourceType.FUEL, 3 );
        TestUtils.createObjectForModules( 1, 0, smallConnectedContainerModule );

        // Now we should be able to mine enough to fill the container.
        assertEquals( smallConnectedContainerModule.getAvailable(), 3 );
        extractorModule.onNewTurn();
        assertEquals( extractorModule.getGameObject().getLocation().getResourceQuantity( ResourceType.FUEL ), 7 );
        assertEquals( smallConnectedContainerModule.getAvailable(), 0 );
        extractorModule.onNewTurn();
        assertEquals( extractorModule.getGameObject().getLocation().getResourceQuantity( ResourceType.FUEL ), 7 );
        assertEquals( smallConnectedContainerModule.getAvailable(), 0 );

        // Create a bigger connected container unit.
        ContainerModule bigConnectedContainerModule = new ContainerModule( ResourceType.FUEL, 15 );
        TestUtils.createObjectForModules( 0, 1, bigConnectedContainerModule );

        // Now we should be able to mine enough to max out the extractor's speed and then empty the tile.
        assertEquals( smallConnectedContainerModule.getAvailable(), 0 );
        assertEquals( bigConnectedContainerModule.getAvailable(), 15 );
        extractorModule.onNewTurn();
        assertEquals( extractorModule.getGameObject().getLocation().getResourceQuantity( ResourceType.FUEL ), 2 );
        assertEquals( bigConnectedContainerModule.getAvailable(), 10 );
        extractorModule.onNewTurn();
        assertEquals( extractorModule.getGameObject().getLocation().getResourceQuantity( ResourceType.FUEL ), 0 );
        assertEquals( bigConnectedContainerModule.getAvailable(), 8 );
        assertEquals( bigConnectedContainerModule.getStock(), 7 );
    }
}
