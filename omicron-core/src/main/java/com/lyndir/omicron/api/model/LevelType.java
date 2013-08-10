package com.lyndir.omicron.api.model;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
public enum LevelType {

    GROUND( "Ground", ResourceType.FUEL, ResourceType.SILICON, ResourceType.METALS ),
    SKY( "Sky" ),
    SPACE( "Space", ResourceType.FUEL, ResourceType.SILICON, ResourceType.METALS, ResourceType.RARE_ELEMENTS );

    private final String                            name;
    private final ImmutableCollection<ResourceType> supportedResources;

    LevelType(final String name, final ResourceType... supportedResources) {

        this.name = name;
        this.supportedResources = ImmutableSet.copyOf( supportedResources );
    }

    public String getName() {

        return name;
    }

    public Optional<LevelType> down() {

        int downOrdinal = ordinal() - 1;
        if (downOrdinal < 0)
            return Optional.absent();

        return Optional.of( values()[downOrdinal] );
    }

    public Optional<LevelType> up() {

        int upOrdinal = ordinal() + 1;
        if (upOrdinal >= values().length)
            return Optional.absent();

        return Optional.of( values()[upOrdinal] );
    }

    public static Optional<LevelType> forName(final String name) {

        for (final LevelType levelType : values())
            if (levelType.getName().equalsIgnoreCase( name ))
                return Optional.of( levelType );

        return Optional.absent();
    }

    public ImmutableCollection<ResourceType> getSupportedResources() {

        return supportedResources;
    }
}
