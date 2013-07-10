package com.lyndir.omnicron.api.controller;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.lyndir.omnicron.api.model.*;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class BaseModule extends Module implements GameObserver {

    private final int                         maxHealth;
    private final int                         armor;
    private final int                         viewRange;
    private final Set<Class<? extends Level>> supportedLayers;
    private       int                         damage;

    public BaseModule(final int maxHealth, final int armor, final int viewRange, final Set<Class<? extends Level>> supportedLayers) {

        this.maxHealth = maxHealth;
        this.armor = armor;
        this.viewRange = viewRange;
        this.supportedLayers = supportedLayers;
    }

    @Override
    public boolean canObserve(@NotNull final Player currentPlayer, @NotNull final Tile location) {

        Player owner = getGameObject().getPlayer();
        if (owner != null && !owner.equals( currentPlayer ))
            return false;

        return getGameObject().getLocation().getPosition().distanceTo( location.getPosition() ) <= viewRange;
    }

    @NotNull
    @Override
    public Iterable<Tile> listObservableTiles(@NotNull final Player currentPlayer) {

        return FluentIterable.from( getGameObject().getLocation().getLevel().getTiles().values() ).filter( new Predicate<Tile>() {
            @Override
            public boolean apply(final Tile input) {

                return canObserve( currentPlayer, input );
            }
        } );
    }

    @Nullable
    @Override
    public Player getPlayer() {

        return getGameObject().getPlayer();
    }

    public int getMaxHealth() {

        return maxHealth;
    }

    public int getRemainingHealth() {

        return Math.max( 0, maxHealth - damage );
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

    @Override
    public void newTurn() {

    }

    public void addDamage(final int incomingDamage) {

        damage += Math.max( 0, incomingDamage - armor );

        if (getRemainingHealth() <= 0)
            getGameObject().getController().die();
    }
}
