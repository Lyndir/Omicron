package com.lyndir.omicron.api.model;

import com.google.common.base.Optional;


public enum ResourceType {
    FUEL( "Fu" ), SILICON( "Si" ), METALS( "Mt" ), RARE_ELEMENTS( "Re" );
    private final String abbreviation;

    ResourceType(final String abbreviation) {

        this.abbreviation = abbreviation;
    }

    public String getAbbreviation() {

        return abbreviation;
    }

    public static Optional<ResourceType> forAbbreviation(final String abbreviation) {

        for (final ResourceType resourceType : values())
            if (resourceType.getAbbreviation().equalsIgnoreCase( abbreviation ))
                return Optional.of( resourceType );

        return Optional.absent();
    }
}
