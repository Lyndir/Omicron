package com.lyndir.omicron.api.model;

import static com.lyndir.omicron.api.util.PathUtils.*;


public interface IMobilityModule extends IModule {

    @Override
    PublicModuleType<? extends IMobilityModule> getType();

    double getRemainingSpeed()
            throws Security.NotAuthenticatedException, Security.NotObservableException;

    /**
     * Get the speed cost related to moving around in the given level.
     *
     * @param levelType The level to move around in.
     *
     * @return The speed cost.
     */
    double costForMovingInLevel(LevelType levelType)
            throws Security.NotAuthenticatedException, Security.NotObservableException;

    /**
     * Get the speed cost related to leveling from the current level type to the given level type.
     *
     * @param levelType The level to transition to.
     *
     * @return The speed cost.
     */
    double costForLevelingToLevel(LevelType levelType)
            throws Security.NotAuthenticatedException, Security.NotObservableException;

    /**
     * Move the unit to the given level.
     *
     * @param levelType The side of the adjacent tile relative to the current.
     */
    ILeveling leveling(LevelType levelType)
            throws Security.NotAuthenticatedException, Security.NotOwnedException, Security.NotObservableException;

    /**
     * Move the unit to an adjacent tile.
     *
     * @param target The side of the adjacent tile relative to the current.
     */
    IMovement movement(ITile target)
            throws Security.NotAuthenticatedException, Security.NotOwnedException, Security.NotObservableException;

    interface ILeveling {

        boolean isPossible();

        /**
         * The cost for executing the leveling.  If not possible, the cost is either zero if unknown or the cost for the action that
         * exceeded the module's remaining speed (not the cost of getting to the target).
         *
         * @return An amount of speed.
         */
        double getCost();

        /**
         * @return The target tile after leveling.
         *
         * @throws IllegalStateException if the leveling is not possible ({@link #isPossible()} returns {@code false})
         */
        ITile getTarget();

        void execute()
                throws Security.NotAuthenticatedException, Security.NotOwnedException, ImpossibleException, InvalidatedException,
                       Security.NotObservableException;
    }


    interface IMovement {

        /**
         * The cost for executing the movement.  If not possible, the cost is either zero if unknown or the cost for the action that
         * exceeded the module's remaining speed (not the cost of getting to the target).
         *
         * @return An amount of speed.
         */
        double getCost();

        /**
         * @return The target tile after leveling.
         *
         * @throws IllegalStateException if the leveling is not possible ({@link #isPossible()} returns {@code false})
         */
        Path<? extends ITile> getPath();

        boolean isPossible();

        void execute()
                throws Security.NotAuthenticatedException, Security.NotOwnedException, ImpossibleException, InvalidatedException,
                       Security.NotObservableException, ImpossibleException;
    }
}
