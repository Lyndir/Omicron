package com.lyndir.omicron.api.controller;

import com.lyndir.omicron.api.model.ResourceType;
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

    }
}
