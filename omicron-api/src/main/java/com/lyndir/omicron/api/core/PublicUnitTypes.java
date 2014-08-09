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

/**
 * @author lhunath, 2013-08-03
 */
public enum PublicUnitTypes {

    /**
     * An engineer is the base construction unit.
     */
    ENGINEER,

    /**
     * A scout is a cheap, fast, long-range but weak land-based scouting unit.
     */
    SCOUT,

    /**
     * An airship is a fast, long-range but weak airborne scouting unit.
     */
    AIRSHIP,

    /**
     * A container provides capacity for storing a variety of resources.
     */
    CONTAINER,

    /**
     * A quarry is a facility for mining metal resources.
     */
    QUARRY,

    /**
     * A drill is a facility for mining fuel resources.
     */
    DRILL,

    /**
     * A construction site is a temporary site while a unit is being constructed.
     */
    CONSTRUCTION
}
