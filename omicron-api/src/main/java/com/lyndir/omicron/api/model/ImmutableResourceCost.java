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
import java.util.Map;


/**
 * @author lhunath, 2013-08-04
 */
public class ImmutableResourceCost extends ResourceCost {

    private final ImmutableMap<ResourceType, Integer> resourceQuantities;

    protected ImmutableResourceCost() {
        this( ImmutableMap.<ResourceType, Integer>of() );
    }

    protected ImmutableResourceCost(final Map<ResourceType, Integer> resourceQuantities) {
        this.resourceQuantities = ImmutableMap.copyOf( resourceQuantities );
    }

    @Override
    protected ImmutableMap<ResourceType, Integer> getResourceQuantities() {
        return resourceQuantities;
    }

    @Override
    public ImmutableResourceCost reduce(final ResourceType resourceType, final int term) {
        Preconditions.checkArgument( term >= 0, "Amount to reduce resource cost with must be positive." );
        return new ImmutableResourceCost( ImmutableMap.<ResourceType, Integer>builder()
                                                      .putAll( resourceQuantities )
                                                      .put( resourceType, get( resourceType ) - term )
                                                      .build() );
    }

    @Override
    public ImmutableResourceCost add(final ResourceType resourceType, final int term) {
        Preconditions.checkArgument( term >= 0, "Amount to increase resource cost with must be positive." );
        return new ImmutableResourceCost( ImmutableMap.<ResourceType, Integer>builder()
                                                      .putAll( resourceQuantities )
                                                      .put( resourceType, get( resourceType ) + term )
                                                      .build() );
    }

    @Override
    public ImmutableResourceCost add(final ResourceCost resourceCost) {
        ImmutableMap.Builder<ResourceType, Integer> builder = ImmutableMap.builder();
        for (final ResourceType resourceType : ResourceType.values())
            builder.put( resourceType, get( resourceType ) + resourceCost.get( resourceType ) );

        return new ImmutableResourceCost( builder.build() );
    }

    @Override
    public ImmutableResourceCost multiply(final ResourceType resourceType, final int factor) {
        return new ImmutableResourceCost( ImmutableMap.<ResourceType, Integer>builder()
                                                      .putAll( resourceQuantities )
                                                      .put( resourceType, get( resourceType ) * factor )
                                                      .build() );
    }

    @Override
    public ImmutableResourceCost multiply(final int factor) {
        ImmutableMap.Builder<ResourceType, Integer> builder = ImmutableMap.builder();
        for (final ResourceType resourceType : ResourceType.values())
            builder.put( resourceType, get( resourceType ) * factor );

        return new ImmutableResourceCost( builder.build() );
    }
}
