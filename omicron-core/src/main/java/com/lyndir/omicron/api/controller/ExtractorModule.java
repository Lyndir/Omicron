package com.lyndir.omicron.api.controller;

import com.lyndir.omicron.api.model.ResourceType;
import java.util.Random;


public class ExtractorModule extends Module {

    private static final Random RANDOM = new Random();

    private final ResourceType resourceType;
    private final int          speed;

    public ExtractorModule(final ResourceType resourceType, final int speed) {

        this.resourceType = resourceType;
        this.speed = speed;
    }

    @Override
    public void onNewTurn() {

    }
}
