package com.lyndir.omnicron.api;

import com.google.common.collect.ImmutableSet;
import com.lyndir.lhunath.opal.system.util.MetaObject;
import java.util.Set;
import org.jetbrains.annotations.NotNull;


/**
 * <i>10 15, 2012</i>
 *
 * @author lhunath
 */
public class GameObject extends MetaObject implements GameObserver {

    private final Player owningPlayer;
    private final Tile locationTile;

    public GameObject(final Player owningPlayer, final Tile locationTile) {

        this.owningPlayer = owningPlayer;
        this.locationTile = locationTile;
    }

    @Override
    public GameObserver getParent() {

        return owningPlayer;
    }

    @NotNull
    @Override
    public Player getPlayer() {

        return owningPlayer;
    }

    @NotNull
    @Override
    public Set<Tile> getObservedTiles() {

        return ImmutableSet.of( locationTile );
    }

    public Player getOwningPlayer() {

        return owningPlayer;
    }

    public Tile getLocationTile() {

        return locationTile;
    }
}
