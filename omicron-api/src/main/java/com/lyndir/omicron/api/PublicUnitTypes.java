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


package com.lyndir.omicron.api;


/**
 * @author lhunath, 2013-08-03
 */
public enum PublicUnitTypes implements IUnitType {

    /**
     * An engineer is the base construction unit.
     */
    ENGINEER( "Engineer", 5 ),

    /**
     * A scout is a cheap, fast, long-range but weak land-based scouting unit.
     */
    SCOUT( "Scout", 5 ),

    /**
     * An airship is a fast, long-range but weak airborne scouting unit.
     */
    AIRSHIP( "Airship", 10 ),

    /**
     * A container provides capacity for storing a variety of resources.
     */
    CONTAINER( "Container", 5 ),

    /**
     * A quarry is a facility for mining metal resources.
     */
    QUARRY( "Quarry", 5 ),

    /**
     * A drill is a facility for mining fuel resources.
     */
    DRILL( "Drill Site", 5 ),

    /**
     * A construction site is a temporary site while a unit is being constructed.
     */
    CONSTRUCTION( "Construction Site", Integer.MAX_VALUE );

    private final String typeName;
    private final int    constructionWork;

    PublicUnitTypes(final String typeName, final int constructionWork) {
        this.typeName = typeName;
        this.constructionWork = constructionWork;
    }

    @Override
    public String getTypeName() {
        return typeName;
    }

    @Override
    public int getConstructionWork() {
        return constructionWork;
    }
}
