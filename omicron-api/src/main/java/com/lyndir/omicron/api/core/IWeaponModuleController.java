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

import com.lyndir.omicron.api.core.error.OmicronException;


/**
 * @author lhunath, 2014-08-09
 */
public interface IWeaponModuleController extends IModuleController<IWeaponModule> {

    boolean fireAt(ITile target)
            throws Security.NotAuthenticatedException, Security.NotOwnedException, Security.NotObservableException, OutOfRangeException, OutOfRepeatsException,
                   OutOfAmmunitionException;

    class OutOfRangeException extends OmicronException {

        OutOfRangeException() {
            super( "The target is out of range for this weapon." );
        }
    }


    class OutOfRepeatsException extends OmicronException {

        OutOfRepeatsException() {
            super( "The weapon cannot repeat anymore." );
        }
    }


    class OutOfAmmunitionException extends OmicronException {

        OutOfAmmunitionException() {
            super( "The weapon is out of ammunition." );
        }
    }
}
