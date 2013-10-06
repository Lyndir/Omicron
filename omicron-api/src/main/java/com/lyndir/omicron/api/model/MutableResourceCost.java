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

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.util.*;


/**
 * @author lhunath, 2013-08-04
 */
public class MutableResourceCost extends ResourceCost {

    private final Map<ResourceType, Integer> resourceQuantities = Collections.synchronizedMap(
            new EnumMap<ResourceType, Integer>( ResourceType.class ) );

    protected MutableResourceCost() {
        this( ImmutableMap.<ResourceType, Integer>of() );
    }

    protected MutableResourceCost(final Map<ResourceType, Integer> resourceQuantities) {
        this.resourceQuantities.putAll( resourceQuantities );
    }

    @Override
    protected ImmutableMap<ResourceType, Integer> getResourceQuantities() {
        return ImmutableMap.copyOf( resourceQuantities );
    }

    /**
     * {@inheritDoc}
     *
     * @return This resource cost after applying the mutation.
     */
    @Override
    public MutableResourceCost reduce(final ResourceType resourceType, final int term) {
        Preconditions.checkArgument( term >= 0, "Amount to reduce resource cost with must be positive." );
        resourceQuantities.put( resourceType, get( resourceType ) - term );

        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @return This resource cost after applying the mutation.
     */
    @Override
    public MutableResourceCost add(final ResourceType resourceType, final int term) {
        Preconditions.checkArgument( term >= 0, "Amount to increase resource cost with must be positive." );
        resourceQuantities.put( resourceType, get( resourceType ) + term );

        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @return This resource cost after applying the mutation.
     */
    @Override
    public MutableResourceCost add(final ResourceCost resourceCost) {
        for (final ResourceType resourceType : ResourceType.values())
            add( resourceType, resourceCost.get( resourceType ) );

        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @return This resource cost after applying the mutation.
     */
    @Override
    public MutableResourceCost multiply(final ResourceType resourceType, final int factor) {
        resourceQuantities.put( resourceType, get( resourceType ) * factor );

        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @return This resource cost after applying the mutation.
     */
    @Override
    public MutableResourceCost multiply(final int factor) {
        for (final ResourceType resourceType : ResourceType.values())
            multiply( resourceType, factor );

        return this;
    }
}
