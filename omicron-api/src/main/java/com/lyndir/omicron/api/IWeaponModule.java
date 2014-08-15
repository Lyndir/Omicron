package com.lyndir.omicron.api;

import com.google.common.collect.ImmutableSet;


public interface IWeaponModule extends IModule {

    @Override
    default PublicModuleType<? extends IWeaponModule> getType() {
        return PublicModuleType.WEAPON;
    }

    /**
     * @return The amount of damaging power this weapon provides in a single shot.
     */
    int getFirePower();

    /**
     * @return The amount of extra damaging power this weapon is able to contribute.
     */
    int getVariance();

    /**
     * @return The distance to the farthest tile from this unit's current location this weapon is able to strike.
     */
    int getRange();

    /**
     * @return The amount of times in a single turn this weapon is able to take a shot.
     */
    int getRepeat();

    /**
     * @return The total amount of ammunition this weapon is able to be loaded with.
     */
    int getAmmunitionLoad();

    /**
     * @return The type of levels this weapon is able to strike at.
     */
    ImmutableSet<LevelType> getSupportedLayers();

    /**
     * @return The amount of times this weapon has fired in the current turn.
     */
    int getRepeated();

    /**
     * @return The amount of remaining ammunition available to the weapon.
     */
    int getAmmunition();

    @Override
    IWeaponModuleController getController();
}
