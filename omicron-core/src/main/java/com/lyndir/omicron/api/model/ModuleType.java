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

import com.lyndir.omicron.api.controller.*;


/**
 * @author lhunath, 2013-08-02
 */
public abstract class ModuleType<M extends Module> {

    public static final ModuleType<ExtractorModule>   EXTRACTOR   = new ModuleType<ExtractorModule>( ExtractorModule.class ) {};
    public static final ModuleType<ContainerModule>   CONTAINER   = new ModuleType<ContainerModule>( ContainerModule.class ) {};
    public static final ModuleType<MobilityModule>    MOBILITY    = new ModuleType<MobilityModule>( MobilityModule.class ) {};
    public static final ModuleType<ConstructorModule> CONSTRUCTOR = new ModuleType<ConstructorModule>( ConstructorModule.class ) {};
    public static final ModuleType<BaseModule>        BASE        = new ModuleType<BaseModule>( BaseModule.class ) {};
    public static final ModuleType<WeaponModule>      WEAPON      = new ModuleType<WeaponModule>( WeaponModule.class ) {};

    private final Class<M> moduleType;

    ModuleType(final Class<M> moduleType) {
        this.moduleType = moduleType;
    }

    public Class<M> getModuleType() {
        return moduleType;
    }
}
