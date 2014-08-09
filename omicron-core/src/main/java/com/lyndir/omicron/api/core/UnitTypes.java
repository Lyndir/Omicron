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

import com.google.common.collect.*;
import com.lyndir.lhunath.opal.system.collection.SSupplier;


/**
 * @author lhunath, 2013-08-03
 */
public enum UnitTypes implements UnitType {

    ENGINEER( "Engineer", 5, //
              new SSupplier<ImmutableList<? extends Module>>() {
                  @Override
                  public ImmutableList<? extends Module> get() {
                      return ImmutableList.of( BaseModule.createWithStandardResourceCost()
                                                         .maxHealth( 10 )
                                                         .armor( 2 )
                                                         .viewRange( 3 )
                                                         .supportedLayers( ImmutableSet.of( LevelType.GROUND ) ),
                                               MobilityModule.createWithStandardResourceCost()
                                                             .movementSpeed( 5 )
                                                             .movementCost( ImmutableMap.of( LevelType.GROUND, 1d ) )
                                                             .levelingCost( ImmutableMap.<LevelType, Double>of() ),
                                               ConstructorModule.createWithStandardResourceCost()
                                                                .buildSpeed( 3 )
                                                                .buildsModule( ModuleType.BASE ),
                                               ConstructorModule.createWithStandardResourceCost()
                                                                .buildSpeed( 2 )
                                                                .buildsModule( ModuleType.CONSTRUCTOR ),
                                               ConstructorModule.createWithStandardResourceCost()
                                                                .buildSpeed( 3 )
                                                                .buildsModule( ModuleType.CONTAINER ),
                                               ConstructorModule.createWithStandardResourceCost()
                                                                .buildSpeed( 3 )
                                                                .buildsModule( ModuleType.EXTRACTOR ),
                                               ConstructorModule.createWithStandardResourceCost()
                                                                .buildSpeed( 3 )
                                                                .buildsModule( ModuleType.MOBILITY ),
                                               ConstructorModule.createWithStandardResourceCost()
                                                                .buildSpeed( 2 )
                                                                .buildsModule( ModuleType.WEAPON ),
                                               ContainerModule.createWithStandardResourceCost()
                                                              .resourceType( ResourceType.METALS )
                                                              .capacity( 10 ), //
                                               ContainerModule.createWithStandardResourceCost()
                                                              .resourceType( ResourceType.FUEL )
                                                              .capacity( 5 ), //
                                               ContainerModule.createWithStandardResourceCost()
                                                              .resourceType( ResourceType.RARE_ELEMENTS )
                                                              .capacity( 5 ), //
                                               ContainerModule.createWithStandardResourceCost()
                                                              .resourceType( ResourceType.SILICON )
                                                              .capacity( 5 )
                      );
                  }
              }
    ),
    QUARRY( "Quarry", 5, //
            new SSupplier<ImmutableList<? extends Module>>() {
                @Override
                public ImmutableList<? extends Module> get() {
                    return ImmutableList.of( BaseModule.createWithStandardResourceCost()
                                                       .maxHealth( 20 )
                                                       .armor( 5 )
                                                       .viewRange( 2 )
                                                       .supportedLayers( ImmutableSet.of( LevelType.GROUND ) ),
                                             ExtractorModule.createWithStandardResourceCost().resourceType( ResourceType.METALS ).speed( 2 )
                    );
                }
            }
    ),
    DRILL( "Drill Site", 5, //
           new SSupplier<ImmutableList<? extends Module>>() {
               @Override
               public ImmutableList<? extends Module> get() {
                   return ImmutableList.of( BaseModule.createWithStandardResourceCost()
                                                      .maxHealth( 20 )
                                                      .armor( 2 )
                                                      .viewRange( 2 )
                                                      .supportedLayers( ImmutableSet.of( LevelType.GROUND ) ),
                                            ExtractorModule.createWithStandardResourceCost().resourceType( ResourceType.FUEL ).speed( 2 )
                   );
               }
           }
    ),
    CONTAINER( "Container", 5, //
               new SSupplier<ImmutableList<? extends Module>>() {
                   @Override
                   public ImmutableList<? extends Module> get() {
                       return ImmutableList.of( BaseModule.createWithStandardResourceCost()
                                                          .maxHealth( 20 )
                                                          .armor( 10 )
                                                          .viewRange( 2 )
                                                          .supportedLayers( ImmutableSet.of( LevelType.GROUND ) ),
                                                ContainerModule.createWithStandardResourceCost()
                                                               .resourceType( ResourceType.METALS )
                                                               .capacity( 20 ), //
                                                ContainerModule.createWithStandardResourceCost()
                                                               .resourceType( ResourceType.FUEL )
                                                               .capacity( 20 ), //
                                                ContainerModule.createWithStandardResourceCost()
                                                               .resourceType( ResourceType.RARE_ELEMENTS )
                                                               .capacity( 20 ), //
                                                ContainerModule.createWithStandardResourceCost()
                                                               .resourceType( ResourceType.SILICON )
                                                               .capacity( 20 )
                       );
                   }
               }
    ),
    SCOUT( "Scout", 5, //
           new SSupplier<ImmutableList<? extends Module>>() {
               @Override
               public ImmutableList<? extends Module> get() {
                   return ImmutableList.of( BaseModule.createWithStandardResourceCost()
                                                      .maxHealth( 5 )
                                                      .armor( 3 )
                                                      .viewRange( 5 )
                                                      .supportedLayers( ImmutableSet.of( LevelType.GROUND ) ),
                                            MobilityModule.createWithStandardResourceCost()
                                                          .movementSpeed( 8 )
                                                          .movementCost( ImmutableMap.of( LevelType.GROUND, 1d ) )
                                                          .levelingCost( ImmutableMap.<LevelType, Double>of() ),
                                            WeaponModule.createWithStandardResourceCost()
                                                        .firePower( 3 )
                                                        .armor( 3 )
                                                        .range( 5 )
                                                        .repeat( 3 )
                                                        .ammunitionLoad( 20 )
                                                        .supportedLayers( ImmutableSet.of( LevelType.GROUND ) )
                   );
               }
           }
    ),
    AIRSHIP( "Airship", 10, //
             new SSupplier<ImmutableList<? extends Module>>() {
                 @Override
                 public ImmutableList<? extends Module> get() {
                     return ImmutableList.of( BaseModule.createWithStandardResourceCost()
                                                        .maxHealth( 5 )
                                                        .armor( 1 )
                                                        .viewRange( 5 )
                                                        .supportedLayers( ImmutableSet.of( LevelType.SKY ) ),
                                              MobilityModule.createWithStandardResourceCost()
                                                            .movementSpeed( 2 )
                                                            .movementCost( ImmutableMap.of( LevelType.SKY, 1d ) )
                                                            .levelingCost( ImmutableMap.<LevelType, Double>of() )
                     );
                 }
             }
    ),
    CONSTRUCTION( "Construction Site", Integer.MAX_VALUE, //
                  new SSupplier<ImmutableList<? extends Module>>() {
                      @Override
                      public ImmutableList<? extends Module> get() {
                          return ImmutableList.of( BaseModule.createWithStandardResourceCost()
                                                             .maxHealth( 1 )
                                                             .armor( 1 )
                                                             .viewRange( 1 )
                                                             .supportedLayers( ImmutableSet.copyOf( LevelType.values() ) ) );
                      }
                  }
    );
    private final String                                     typeName;
    private final int                                        constructionWork;
    private final SSupplier<ImmutableList<? extends Module>> moduleSupplier;

    UnitTypes(final String typeName, final int constructionWork, final SSupplier<ImmutableList<? extends Module>> moduleSupplier) {
        this.typeName = typeName;
        this.constructionWork = constructionWork;
        this.moduleSupplier = moduleSupplier;
    }

    @Override
    public String getTypeName() {
        return typeName;
    }

    @Override
    public int getConstructionWork() {
        return constructionWork;
    }

    @Override
    public ImmutableList<? extends Module> createModules() {
        return moduleSupplier.get();
    }
}
