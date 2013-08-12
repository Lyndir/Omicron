package com.lyndir.omicron.api.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class Turn {

    @Nullable
    private final Turn previousTurn;
    private final int  number;

    Turn() {
        previousTurn = null;
        number = 0;
    }

    Turn(@Nonnull final Turn previousTurn) {
        this.previousTurn = previousTurn;
        number = previousTurn.getNumber() + 1;
    }

    @Nullable
    public Turn getPreviousTurn() {
        return previousTurn;
    }

    public int getNumber() {
        return number;
    }
}
