package com.lyndir.omicron.api.controller;

import com.lyndir.omicron.api.model.ResourceType;
import com.lyndir.omicron.api.model.Tile;
import java.util.Random;


public class ExtractorModule extends Module {

    private final ResourceType resourceType;
    private final int          speed;

    public ExtractorModule(final ResourceType resourceType, final int speed) {

        this.resourceType = resourceType;
        this.speed = speed;
    }

    public ResourceType getResourceType() {

        return resourceType;
    }

    public int getSpeed() {

        return speed;
    }

    @Override
    public void onNewTurn() {

        Tile location = getGameObject().getLocation();
        int availableResources = location.getResourceQuantity( resourceType );
        int newAvailableResources = Math.max(0, availableResources - speed);
        int minedResources = availableResources - newAvailableResources;
        if (minedResources == 0)
            return;

        // TODO: Send minedResources somewhere.
        location.setResourceQuantity( resourceType, newAvailableResources );
    }
}
