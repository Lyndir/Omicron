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

import com.lyndir.lhunath.opal.system.util.MetaObject;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * @author lhunath, 2013-08-02
 */
public abstract class PublicModuleType<M extends IModule> extends MetaObject {

    /**
     * Makes a unit destructible and gives it the ability to observe its surroundings.
     */
    public static final PublicModuleType<IBaseModule> BASE = //
            new PublicModuleType<IBaseModule>( IBaseModule.class, ResourceCost.immutableOf( ResourceType.METALS, 1 ) ) {};

    /**
     * Gives the unit the ability to move around.
     */
    public static final PublicModuleType<IMobilityModule> MOBILITY = //
            new PublicModuleType<IMobilityModule>( IMobilityModule.class, ResourceCost.immutableOf( ResourceType.METALS, 1 ) ) {};

    /**
     * Provides the unit with resource storage.
     */
    public static final PublicModuleType<IContainerModule> CONTAINER = //
            new PublicModuleType<IContainerModule>( IContainerModule.class, ResourceCost.immutableOf( ResourceType.METALS, 1 ) ) {};

    /**
     * Adds the provisions for extracting resources to the unit.
     */
    public static final PublicModuleType<IExtractorModule> EXTRACTOR = //
            new PublicModuleType<IExtractorModule>( IExtractorModule.class, ResourceCost.immutableOf( ResourceType.METALS, 1 ) ) {};

    /**
     * Teaches the unit how to construct other units.
     */
    public static final PublicModuleType<IConstructorModule> CONSTRUCTOR = //
            new PublicModuleType<IConstructorModule>( IConstructorModule.class, ResourceCost.immutableOf( ResourceType.METALS, 2 ) ) {};

    /**
     * Installs a weapon system on the unit, allowing it to inflict damage onto other units.
     */
    public static final PublicModuleType<IWeaponModule> WEAPON = //
            new PublicModuleType<IWeaponModule>( IWeaponModule.class, ResourceCost.immutableOf( ResourceType.METALS, 2 ) ) {};

    private final Class<M>              moduleType;
    private final ImmutableResourceCost standardCost;

    PublicModuleType(@Nonnull final Class<M> moduleType, @Nonnull final ImmutableResourceCost standardCost) {
        this.moduleType = moduleType;
        this.standardCost = standardCost;
    }

    @Nonnull
    public Class<M> getModuleType() {
        return moduleType;
    }

    @Nonnull
    public ImmutableResourceCost getStandardCost() {
        return standardCost;
    }

    @Override
    public int hashCode() {
        return Objects.hash( moduleType, standardCost );
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof PublicModuleType))
            return false;

        PublicModuleType<?> o = (PublicModuleType<?>) obj;
        return moduleType.equals( o.moduleType ) && standardCost.equals( o.standardCost );
    }
}
