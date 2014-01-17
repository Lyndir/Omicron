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
import java.util.Objects;


/**
 * @author lhunath, 2013-08-02
 */
public abstract class PublicModuleType<M extends IModule> extends MetaObject {

    public static final PublicModuleType<IExtractorModule>   EXTRACTOR   = //
            new PublicModuleType<IExtractorModule>( IExtractorModule.class, ResourceCost.immutableOf( ResourceType.METALS, 1 ) ) {};
    public static final PublicModuleType<IContainerModule>   CONTAINER   = //
            new PublicModuleType<IContainerModule>( IContainerModule.class, ResourceCost.immutableOf( ResourceType.METALS, 1 ) ) {};
    public static final PublicModuleType<IMobilityModule>    MOBILITY    = //
            new PublicModuleType<IMobilityModule>( IMobilityModule.class, ResourceCost.immutableOf( ResourceType.METALS, 1 ) ) {};
    public static final PublicModuleType<IConstructorModule> CONSTRUCTOR = //
            new PublicModuleType<IConstructorModule>( IConstructorModule.class, ResourceCost.immutableOf( ResourceType.METALS, 2 ) ) {};
    public static final PublicModuleType<IBaseModule>        BASE        = //
            new PublicModuleType<IBaseModule>( IBaseModule.class, ResourceCost.immutableOf( ResourceType.METALS, 1 ) ) {};
    public static final PublicModuleType<IWeaponModule>      WEAPON      = //
            new PublicModuleType<IWeaponModule>( IWeaponModule.class, ResourceCost.immutableOf( ResourceType.METALS, 2 ) ) {};

    private final Class<M>              moduleType;
    private final ImmutableResourceCost standardCost;

    PublicModuleType(final Class<M> moduleType, final ImmutableResourceCost standardCost) {
        this.moduleType = moduleType;
        this.standardCost = standardCost;
    }

    public Class<M> getModuleType() {
        return moduleType;
    }

    public ImmutableResourceCost getStandardCost() {
        return standardCost;
    }

    @Override
    public int hashCode() {
        return Objects.hash( moduleType, standardCost );
    }
}
