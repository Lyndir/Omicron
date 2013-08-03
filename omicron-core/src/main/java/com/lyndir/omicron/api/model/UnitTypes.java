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

import com.google.common.base.Supplier;
import com.google.common.collect.*;
import com.lyndir.omicron.api.controller.*;
import java.util.List;


/**
 * @author lhunath, 2013-08-03
 */
public enum UnitTypes implements UnitType {

    ENGINEER( "Engineer", 5, //
              new Supplier<List<Module>>() {
                  @Override
                  public List<Module> get() {
                      return ImmutableList.of( new BaseModule( 10, 2, 3, ImmutableSet.of( LevelType.GROUND ) ),
                                               new MobilityModule( 5, ImmutableMap.of( LevelType.GROUND, 1d ),
                                                                   ImmutableMap.<LevelType, Double>of() ) );
                  }
              } ),
    SCOUT( "Scout", 5, //
           new Supplier<List<Module>>() {
               @Override
               public List<Module> get() {
                   return ImmutableList.of( new BaseModule( 5, 3, 5, ImmutableSet.of( LevelType.GROUND ) ),
                                            new MobilityModule( 8, ImmutableMap.of( LevelType.GROUND, 1d ),
                                                                ImmutableMap.<LevelType, Double>of() ),
                                            new WeaponModule( 3, 3, 5, 3, 20, ImmutableSet.of( LevelType.GROUND ) ) );
               }
           } ),
    AIRSHIP( "Airship", 10, //
             new Supplier<List<Module>>() {
                 @Override
                 public List<Module> get() {
                     return ImmutableList.of( new BaseModule( 5, 1, 5, ImmutableSet.of( LevelType.SKY ) ),
                                              new MobilityModule( 2, ImmutableMap.of( LevelType.SKY, 1d ),
                                                                  ImmutableMap.<LevelType, Double>of() ) );
                 }
             } ),
    CONSTRUCTION( "Construction Site", Integer.MAX_VALUE, //
                  new Supplier<List<Module>>() {
                      @Override
                      public List<Module> get() {
                          return ImmutableList.<Module>of( new BaseModule( 1, 1, 1, ImmutableSet.copyOf( LevelType.values() ) ) );
                      }
                  } );

    private final String                 typeName;
    private final int                    complexity;
    private final Supplier<List<Module>> moduleSupplier;

    UnitTypes(final String typeName, final int complexity, final Supplier<List<Module>> moduleSupplier) {
        this.typeName = typeName;
        this.complexity = complexity;
        this.moduleSupplier = moduleSupplier;
    }

    @Override
    public String getTypeName() {
        return typeName;
    }

    @Override
    public int getComplexity() {
        return complexity;
    }

    @Override
    public List<Module> createModules() {
        return moduleSupplier.get();
    }
}
