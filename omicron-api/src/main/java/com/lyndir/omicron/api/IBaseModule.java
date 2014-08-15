package com.lyndir.omicron.api;

import com.google.common.collect.ImmutableSet;


public interface IBaseModule extends IModule {

    @Override
    default PublicModuleType<? extends IBaseModule> getType() {
        return PublicModuleType.BASE;
    }

    /**
     * @return The unit's maximum health represents the total amount of damage a unit can receive before it is destroyed.
     */
    int getMaxHealth();

    /**
     * @return The protection this unit has against incoming damage.
     */
    int getArmor();

    /**
     * @return The distance from the unit's current tile that the unit can observe the activity on other tiles.
     */
    int getViewRange();

    /**
     * @return The type of levels on which this unit is able to exist.
     */
    ImmutableSet<LevelType> getSupportedLayers();

    /**
     * @return The total amount of damage this unit has incurred so far.
     */
    int getDamage();

    /**
     * @return The amount of damage this unit can still incur before it will be destroyed.
     */
    default int getRemainingHealth() {
        return Math.max( 0, getMaxHealth() - getDamage() );
    }

    @Override
    IBaseModuleController getController();
}
