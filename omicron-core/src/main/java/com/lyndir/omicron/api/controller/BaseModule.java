package com.lyndir.omicron.api.controller;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.lyndir.omicron.api.model.*;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


// TODO: Should this module's logic be moved to GameObjectController?
// TODO: It uniquely describes a game object and can exist only once and lots of external code assumes there is one and only one...
public class BaseModule extends Module implements GameObserver {

    private final int            maxHealth;
    private final int            armor;
    private final int            viewRange;
    private final Set<LevelType> supportedLayers;
    private       int            damage;

    public BaseModule(final int maxHealth, final int armor, final int viewRange, final Set<LevelType> supportedLayers) {

        this.maxHealth = maxHealth;
        this.armor = armor;
        this.viewRange = viewRange;
        this.supportedLayers = supportedLayers;
    }

    @Override
    public boolean canObserve(@Nonnull final Player currentPlayer, @Nonnull final Tile location) {

        Player owner = getGameObject().getPlayer();
        if (owner != null && !owner.equals( currentPlayer ))
            return false;

        return getGameObject().getLocation().getPosition().distanceTo( location.getPosition() ) <= viewRange;
    }

    @Nonnull
    @Override
    public Iterable<Tile> listObservableTiles(@Nonnull final Player currentPlayer) {

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

    public Set<LevelType> getSupportedLayers() {

        return supportedLayers;
    }

    @Override
    public void onNewTurn() {

    }

    @Override
    public ModuleType<?> getType() {
        return ModuleType.BASE;
    }

    public void addDamage(final int incomingDamage) {

        damage += Math.max( 0, incomingDamage - armor );

        if (getRemainingHealth() <= 0)
            getGameObject().getController().die();
    }
}
