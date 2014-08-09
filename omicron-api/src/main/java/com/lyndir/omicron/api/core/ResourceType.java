package com.lyndir.omicron.api.core;

import com.google.common.base.Optional;


public enum ResourceType {

    /**
     * This resource provides construction and framing capabilities.
     */
    METALS( "Mt" ),

    /**
     * This resource provides operating energy to machinery.
     */
    FUEL( "Fu" ),

    /**
     * This resource permits the construction of circuitry.
     */
    SILICON( "Si" ),

    /**
     * This resource provides extraordinary properties.
     */
    RARE_ELEMENTS( "Re" );

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
