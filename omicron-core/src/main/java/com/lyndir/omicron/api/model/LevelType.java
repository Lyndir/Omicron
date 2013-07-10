package com.lyndir.omicron.api.model;

import com.google.common.base.Optional;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
public enum LevelType {

    GROUND( "Ground" ), SKY( "Sky" ), SPACE( "Space" );

    private final String name;

    LevelType(final String name) {

        this.name = name;
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
}
