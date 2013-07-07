package com.lyndir.omnicron.api.model;

import java.util.Set;


public class BaseModule {

    private final int                         health;
    private final int                         armor;
    private final int                         viewRange;
    private final Set<Class<? extends Level>> supportedLayers;
    private final Player                      owningPlayer;

    protected BaseModule(final int health, final int armor, final int viewRange, final Set<Class<? extends Level>> supportedLayers, final Player owningPlayer) {

        this.health = health;
        this.armor = armor;
        this.viewRange = viewRange;
        this.supportedLayers = supportedLayers;
        this.owningPlayer = owningPlayer;
    }

    public int getHealth() {

        return health;
    }

    public int getArmor() {

        return armor;
    }

    public int getViewRange() {

        return viewRange;
    }

    public Set<Class<? extends Level>> getSupportedLayers() {

        return supportedLayers;
    }

    public Player getOwningPlayer() {

        return owningPlayer;
    }
}
