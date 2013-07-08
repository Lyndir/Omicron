package com.lyndir.omnicron.api.controller;

import com.lyndir.omnicron.api.model.*;
import java.util.Set;


public class BaseModule extends Module {

    private final int                         health;
    private final int                         armor;
    private final int                         viewRange;
    private final Set<Class<? extends Level>> supportedLayers;

    public BaseModule(final int health, final int armor, final int viewRange, final Set<Class<? extends Level>> supportedLayers) {

        this.health = health;
        this.armor = armor;
        this.viewRange = viewRange;
        this.supportedLayers = supportedLayers;
    }

    public boolean canObserve(final Player currentPlayer, final Tile tile) {

        Player owner = getGameObject().getPlayer();
        if (owner != null && !owner.equals( currentPlayer ))
            return false;

        return getGameObject().getLocation().getPosition().distanceTo( tile.getPosition() ) <= viewRange;
    }

    @Override
    public void newTurn() {
    }
}
