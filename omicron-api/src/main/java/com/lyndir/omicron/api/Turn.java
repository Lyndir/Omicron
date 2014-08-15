package com.lyndir.omicron.api;

import com.lyndir.lhunath.opal.system.util.MetaObject;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class Turn extends MetaObject {

    private final int number;

    public Turn() {
        this( 0 );
    }

    public Turn(@Nonnull final Turn previousTurn) {
        this( previousTurn.getNumber() + 1 );
    }

    protected Turn(final int number) {
        this.number = number;
    }

    @Override
    public int hashCode() {
        return number;
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        return obj instanceof Turn && number == ((Turn) obj).number;
    }

    /**
     * @return Each turn has a linearly incrementing identifying integer counter value.
     */
    public int getNumber() {
        return number;
    }
}
