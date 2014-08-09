/*
 * Copyright 2010, Maarten Billemont
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.lyndir.omicron.api.core;

import com.lyndir.omicron.api.util.PathUtils;


/**
 * @author lhunath, 2014-08-09
 */
public interface IMobilityModuleController extends IModuleController<IMobilityModule> {

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
                throws Security.NotAuthenticatedException, Security.NotOwnedException, IModule.ImpossibleException,
                       PathInvalidatedException, Security.NotObservableException;
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
        PathUtils.Path<? extends ITile> getPath();

        boolean isPossible();

        void execute()
                throws Security.NotAuthenticatedException, Security.NotOwnedException, PathInvalidatedException,
                       Security.NotObservableException, IModule.ImpossibleException;
    }


    class PathInvalidatedException extends IModule.InvalidatedException {

        private final PathUtils.Path<ITile> path;

        PathInvalidatedException(final PathUtils.Path<ITile> path) {
            this.path = path;
        }

        /**
         * @return The path to the point where it has become invalidated (target is the invalid tile).
         */
        public PathUtils.Path<ITile> getPath() {
            return path;
        }
    }
}
