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

import com.lyndir.lhunath.opal.system.util.MetaObject;
import com.lyndir.omicron.api.controller.*;


/**
 * @author lhunath, 2013-08-02
 */
public abstract class ModuleType<M extends Module> extends MetaObject {

    public static final ModuleType<ExtractorModule>   EXTRACTOR   = //
            new ModuleType<ExtractorModule>( ExtractorModule.class, ResourceCost.of( ResourceType.METALS, 1 ) ) {};
    public static final ModuleType<ContainerModule>   CONTAINER   = //
            new ModuleType<ContainerModule>( ContainerModule.class, ResourceCost.of( ResourceType.METALS, 1 ) ) {};
    public static final ModuleType<MobilityModule>    MOBILITY    = //
            new ModuleType<MobilityModule>( MobilityModule.class, ResourceCost.of( ResourceType.METALS, 1 ) ) {};
    public static final ModuleType<ConstructorModule> CONSTRUCTOR = //
            new ModuleType<ConstructorModule>( ConstructorModule.class, ResourceCost.of( ResourceType.METALS, 2 ) ) {};
    public static final ModuleType<BaseModule>        BASE        = //
            new ModuleType<BaseModule>( BaseModule.class, ResourceCost.of( ResourceType.METALS, 1 ) ) {};
    public static final ModuleType<WeaponModule>      WEAPON      = //
            new ModuleType<WeaponModule>( WeaponModule.class, ResourceCost.of( ResourceType.METALS, 2 ) ) {};

    private final Class<M>     moduleType;
    private final ResourceCost standardCost;

    ModuleType(final Class<M> moduleType, final ResourceCost standardCost) {
        this.moduleType = moduleType;
        this.standardCost = standardCost;
    }

    public Class<M> getModuleType() {
        return moduleType;
    }

    public ResourceCost getStandardCost() {
        return standardCost;
    }
}
