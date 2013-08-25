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

package com.lyndir.omicron.api.model;

import com.lyndir.omicron.api.model.error.OmicronException;
import com.lyndir.omicron.api.util.PathUtils;


public interface IModule {

    ImmutableResourceCost getResourceCost()
            throws Security.NotAuthenticatedException, Security.NotObservableException;

    IGameObject getGameObject()
            throws Security.NotAuthenticatedException, Security.NotObservableException;

    IGameController getGameController()
            throws Security.NotAuthenticatedException, Security.NotObservableException;

    PublicModuleType<?> getType();

    class ImpossibleException extends OmicronException {

        ImpossibleException() {
            super( "Action not possible." );
        }
    }


    class InvalidatedException extends OmicronException {

        InvalidatedException() {
            super( "State change has invalidated this action." );
        }
    }


    class PathInvalidatedException extends InvalidatedException {

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
