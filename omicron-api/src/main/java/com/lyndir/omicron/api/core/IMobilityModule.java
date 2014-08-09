package com.lyndir.omicron.api.core;

public interface IMobilityModule extends IModule {

    @Override
    default PublicModuleType<? extends IMobilityModule> getType() {
        return PublicModuleType.MOBILITY;
    }

    /**
     * @return The amount of movement power this unit is able to spend in a turn.
     */
    double getMovementSpeed();

    /**
     * @return The amount of movement power remaining for this unit in the current turn.
     */
    double getRemainingSpeed();

    /**
     * @param levelType The level to move around in.
     *
     * @return The speed cost.
     */
    double costForMovingInLevel(LevelType levelType);

    /**
     * Get the speed cost related to leveling from the current level type to the given level type.
     *
     * @param levelType The level to transition to.
     *
     * @return The costs associated for levelling to a destination level from an adjacent level for this unit.
     */
    double costForLevelingToLevel(LevelType levelType);

    @Override
    IMobilityModuleController getController();
}
