package com.lyndir.omicron.api.model;

import com.lyndir.lhunath.opal.system.util.MetaObject;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class Turn extends MetaObject {

    @Nullable
    private final Turn previousTurn;
    private final int  number;

    public Turn() {
        previousTurn = null;
        number = 0;
    }

    public Turn(@Nonnull final Turn previousTurn) {
        this.previousTurn = previousTurn;
        number = previousTurn.getNumber() + 1;
    }

    @Override
    public int hashCode() {
        return number;
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        return obj instanceof Turn && number == ((Turn) obj).number;
    }

    @Nullable
    public Turn getPreviousTurn() {
        return previousTurn;
    }

    public int getNumber() {
        return number;
    }
}
